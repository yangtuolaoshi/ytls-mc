package icu.ytlsnb.ytls.gameplay.plunger.entity;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 放置的小雨云 / 小雷云：粒子表现局部天气，雷云会周期性落雷。
 */
public class WeatherCloudEntity extends Entity {
    public enum CloudKind {
        RAIN,
        THUNDER
    }

    private static final EntityDataAccessor<Integer> KIND =
            SynchedEntityData.defineId(WeatherCloudEntity.class, EntityDataSerializers.INT);

    /** 持续时间 10 秒 */
    private static final int DEFAULT_LIFE_TICKS = 20 * 10;
    private static final int LIGHTNING_INTERVAL = 40;

    private int life = DEFAULT_LIFE_TICKS;

    public WeatherCloudEntity(EntityType<? extends WeatherCloudEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public WeatherCloudEntity(Level level, CloudKind kind) {
        this(RegistryAccess.resolve(RegistryKind.ENTITY, "weather_cloud"), level);
        setKind(kind);
    }

    public CloudKind getKind() {
        int ordinal = entityData.get(KIND);
        CloudKind[] values = CloudKind.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : CloudKind.RAIN;
    }

    public void setKind(CloudKind kind) {
        entityData.set(KIND, kind.ordinal());
    }

    /** 雨/雷效果水平半径，与碰撞箱半宽一致 */
    private double effectRadius() {
        return getBbWidth() * 0.5D;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(KIND, CloudKind.RAIN.ordinal());
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            spawnClientParticles();
            return;
        }
        if (--life <= 0) {
            discard();
            return;
        }
        if (getKind() == CloudKind.THUNDER
                && life % LIGHTNING_INTERVAL == 0
                && level() instanceof ServerLevel serverLevel) {
            double radius = effectRadius();
            int offset = Math.max(0, (int) Math.ceil(radius));
            BlockPos strike = BlockPos.containing(getX(), getY() - 1, getZ())
                    .offset(level().random.nextInt(offset * 2 + 1) - offset,
                            0,
                            level().random.nextInt(offset * 2 + 1) - offset);
            BlockPos ground = serverLevel.getHeightmapPos(
                    net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, strike);
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (bolt != null) {
                bolt.moveTo(Vec3.atBottomCenterOf(ground));
                serverLevel.addFreshEntity(bolt);
            }
        }
    }

    private void spawnClientParticles() {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        double radius = effectRadius();
        for (int i = 0; i < 4; i++) {
            double ox = (random.nextDouble() - 0.5D) * radius * 2.0D;
            double oz = (random.nextDouble() - 0.5D) * radius * 2.0D;
            level().addParticle(ParticleTypes.CLOUD, cx + ox * 0.85D, cy + 0.15D, cz + oz * 0.85D, 0, 0.01D, 0);
            level().addParticle(ParticleTypes.RAIN, cx + ox, cy, cz + oz, 0, -0.4D, 0);
        }
        if (getKind() == CloudKind.THUNDER && random.nextInt(4) == 0) {
            level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                    cx + (random.nextDouble() - 0.5D) * radius,
                    cy + 0.2D,
                    cz + (random.nextDouble() - 0.5D) * radius,
                    0, 0, 0);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        life = tag.contains("Life") ? tag.getInt("Life") : DEFAULT_LIFE_TICKS;
        if (tag.contains("Kind")) {
            setKind(CloudKind.values()[Math.min(tag.getInt("Kind"), CloudKind.values().length - 1)]);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Life", life);
        tag.putInt("Kind", getKind().ordinal());
    }
}
