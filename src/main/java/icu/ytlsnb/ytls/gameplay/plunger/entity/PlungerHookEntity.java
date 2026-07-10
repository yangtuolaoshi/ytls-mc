package icu.ytlsnb.ytls.gameplay.plunger.entity;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.gameplay.plunger.item.PlungerGunItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 马桶塞枪抛出的钩体：飞行、吸附、收回拉拽。
 * 必须自行实现位移与碰撞——基类 {@link Projectile} 不会做这些事。
 */
public class PlungerHookEntity extends Projectile {
    public enum HookMode {
        FLYING,
        ENTITY,
        ITEM,
        BLOCK,
        /** 空钩 / 拉实体：钩体飞回玩家 */
        REELING,
        /** 钩住方块：钩体钉在命中点，把玩家拉过去 */
        GRAPPLING
    }

    private static final EntityDataAccessor<Integer> HOOK_MODE =
            SynchedEntityData.defineId(PlungerHookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HOOKED_ENTITY_ID =
            SynchedEntityData.defineId(PlungerHookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ATTACH_X =
            SynchedEntityData.defineId(PlungerHookEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ATTACH_Y =
            SynchedEntityData.defineId(PlungerHookEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ATTACH_Z =
            SynchedEntityData.defineId(PlungerHookEntity.class, EntityDataSerializers.FLOAT);

    private static final int MAX_FLIGHT_TICKS = 80;
    private static final double MAX_RANGE = 48.0D;
    private static final double REEL_SPEED = 3.2D;
    private static final double PULL_STRENGTH = 1.35D;
    /** 命中面法线外偏，避免模型/连线埋进方块 */
    private static final double SURFACE_OFFSET = 0.2D;
    private static final float AIR_DRAG = 0.99F;
    private static final float WATER_DRAG = 0.8F;

    private int life;
    @Nullable
    private Entity hookedEntity;
    @Nullable
    private BlockPos hookedPos;
    /** 收回前吸附的类型，REELING 时仍按此决定拉谁 */
    private HookMode latchMode = HookMode.FLYING;

    public PlungerHookEntity(EntityType<? extends PlungerHookEntity> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    public PlungerHookEntity(Level level, Player owner) {
        this(RegistryAccess.resolve(RegistryKind.ENTITY, "plunger_hook"), level);
        setOwner(owner);
        Vec3 eye = owner.getEyePosition();
        setPos(eye.x, eye.y - 0.1D, eye.z);
        setYRot(owner.getYRot());
        setXRot(owner.getXRot());
    }

    public void shootFromPlayer(Player player, float power) {
        Vec3 look = player.getLookAngle();
        float velocity = 2.4F + power * 1.6F;
        shoot(look.x, look.y, look.z, velocity, 0.5F);
        setHookMode(HookMode.FLYING);
        latchMode = HookMode.FLYING;
        life = 0;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(HOOK_MODE, HookMode.FLYING.ordinal());
        entityData.define(HOOKED_ENTITY_ID, 0);
        entityData.define(ATTACH_X, 0.0F);
        entityData.define(ATTACH_Y, 0.0F);
        entityData.define(ATTACH_Z, 0.0F);
    }

    public HookMode getHookMode() {
        int ordinal = entityData.get(HOOK_MODE);
        HookMode[] values = HookMode.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : HookMode.FLYING;
    }

    public void setHookMode(HookMode mode) {
        entityData.set(HOOK_MODE, mode.ordinal());
    }

    public Vec3 getAttachPos() {
        return new Vec3(entityData.get(ATTACH_X), entityData.get(ATTACH_Y), entityData.get(ATTACH_Z));
    }

    private void setAttachPos(Vec3 pos) {
        entityData.set(ATTACH_X, (float) pos.x);
        entityData.set(ATTACH_Y, (float) pos.y);
        entityData.set(ATTACH_Z, (float) pos.z);
    }

    @Nullable
    public Entity getHookedEntity() {
        if (level().isClientSide) {
            int id = entityData.get(HOOKED_ENTITY_ID);
            return id > 0 ? level().getEntity(id) : null;
        }
        return hookedEntity;
    }

    @Nullable
    public BlockPos getHookedPos() {
        return hookedPos;
    }

    @Nullable
    public Player getGunOwner() {
        return getOwner() instanceof Player player ? player : null;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 256.0D * 256.0D;
    }

    @Override
    public boolean isNoGravity() {
        // 飞行重力在 tickFlying 里手动施加，其余模式完全无重力
        return true;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (getHookMode() != HookMode.FLYING) {
            return false;
        }
        return super.canHitEntity(entity)
                && entity.isAlive()
                && entity != getOwner()
                && !(entity instanceof PlungerHookEntity);
    }

    @Override
    public void tick() {
        super.tick();
        life++;

        Player owner = getGunOwner();
        if (!level().isClientSide) {
            if (owner == null || !owner.isAlive() || !isGunInHand(owner)) {
                discard();
                return;
            }
            if (position().distanceToSqr(owner.position()) > MAX_RANGE * MAX_RANGE) {
                discard();
                return;
            }
        }

        switch (getHookMode()) {
            case FLYING -> tickFlying();
            case ENTITY, ITEM -> tickLatchedToEntity();
            case BLOCK, GRAPPLING -> tickStuckToSurface();
            case REELING -> tickReeling();
        }

        if (getHookMode() == HookMode.GRAPPLING) {
            tickGrapplingPull();
        }
    }

    private void tickFlying() {
        if (!level().isClientSide && life > MAX_FLIGHT_TICKS) {
            discard();
            return;
        }

        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS
                && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hit)) {
            onHit(hit);
        }

        if (!isAlive() || getHookMode() != HookMode.FLYING) {
            return;
        }

        Vec3 motion = getDeltaMovement();
        double nextX = getX() + motion.x;
        double nextY = getY() + motion.y;
        double nextZ = getZ() + motion.z;

        updateRotation();
        float drag = isInWater() ? WATER_DRAG : AIR_DRAG;
        setDeltaMovement(motion.scale(drag).add(0.0D, -0.03D, 0.0D));
        setPos(nextX, nextY, nextZ);
    }

    private void tickLatchedToEntity() {
        Entity target = resolveHookedEntity();
        if (target == null || !target.isAlive()) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }
        Vec3 attach = target.position();
        if (getHookMode() == HookMode.ENTITY) {
            attach = attach.add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        }
        setPos(attach);
        setDeltaMovement(Vec3.ZERO);
    }

    /** 钉在命中面外偏位置，客户端/服务端都用同步的 attach 点 */
    private void tickStuckToSurface() {
        Vec3 attach = getAttachPos();
        setPos(attach);
        setDeltaMovement(Vec3.ZERO);
    }

    private void tickGrapplingPull() {
        if (level().isClientSide) {
            return;
        }
        Player owner = getGunOwner();
        if (owner == null) {
            discard();
            return;
        }
        Vec3 attach = getAttachPos();
        // 拉向吸附点上方一点，避免把玩家脸埋进方块
        Vec3 pullTarget = attach.add(0.0D, 0.35D, 0.0D);
        pullToward(owner, pullTarget, PULL_STRENGTH);
        if (owner.distanceToSqr(attach) < 2.25D) {
            finishReel(owner);
        }
    }

    private void tickReeling() {
        Player owner = getGunOwner();
        if (owner == null) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }

        if (latchMode == HookMode.ENTITY || latchMode == HookMode.ITEM) {
            Entity target = resolveHookedEntity();
            if (target != null && target.isAlive()) {
                pullToward(target, owner.position().add(0.0D, owner.getBbHeight() * 0.5D, 0.0D), PULL_STRENGTH);
                setPos(latchMode == HookMode.ENTITY
                        ? target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D)
                        : target.position());
                if (!level().isClientSide && target.distanceToSqr(owner) < 2.25D) {
                    finishReel(owner);
                }
                return;
            }
        }

        Vec3 target = owner.getEyePosition().add(owner.getLookAngle().scale(0.4D));
        Vec3 delta = target.subtract(position());
        double distance = delta.length();
        if (distance < 1.0D) {
            if (!level().isClientSide) {
                finishReel(owner);
            }
            return;
        }
        Vec3 motion = delta.normalize().scale(Math.min(REEL_SPEED, distance));
        setDeltaMovement(motion);
        setPos(position().add(motion));
        updateRotation();
    }

    private static void pullToward(Entity entity, Vec3 destination, double strength) {
        Vec3 delta = destination.subtract(entity.position());
        double distance = delta.length();
        if (distance < 0.05D) {
            return;
        }
        Vec3 pull = delta.normalize().scale(Math.min(strength, distance * 0.55D));
        entity.setDeltaMovement(entity.getDeltaMovement().scale(0.2D).add(pull));
        entity.hurtMarked = true;
        entity.hasImpulse = true;
        if (entity instanceof Player player) {
            player.fallDistance = 0.0F;
        }
    }

    public void startReelIn() {
        if (level().isClientSide) {
            return;
        }
        HookMode current = getHookMode();
        if (current == HookMode.REELING || current == HookMode.GRAPPLING) {
            return;
        }
        latchMode = current;
        if (current == HookMode.BLOCK) {
            // 钩体钉住不动，只拉玩家；模式同步到客户端，避免客户端误把钩飞回去
            setHookMode(HookMode.GRAPPLING);
        } else {
            setHookMode(HookMode.REELING);
        }
        setDeltaMovement(Vec3.ZERO);
    }

    private void finishReel(Player player) {
        PlungerGunItem.clearHookForEntity(player, getUUID());
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (getHookMode() != HookMode.FLYING || level().isClientSide) {
            return;
        }
        Entity entity = result.getEntity();
        if (entity instanceof ItemEntity itemEntity) {
            latch(itemEntity, HookMode.ITEM);
        } else {
            latch(entity, HookMode.ENTITY);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (getHookMode() != HookMode.FLYING || level().isClientSide) {
            return;
        }
        hookedPos = result.getBlockPos();
        latchMode = HookMode.BLOCK;
        setHookMode(HookMode.BLOCK);
        setDeltaMovement(Vec3.ZERO);

        Direction face = result.getDirection();
        Vec3 attach = result.getLocation().add(
                face.getStepX() * SURFACE_OFFSET,
                face.getStepY() * SURFACE_OFFSET,
                face.getStepZ() * SURFACE_OFFSET
        );
        setAttachPos(attach);
        setPos(attach);
    }

    private void latch(Entity entity, HookMode mode) {
        hookedEntity = entity;
        latchMode = mode;
        setHookMode(mode);
        entityData.set(HOOKED_ENTITY_ID, entity.getId());
        setDeltaMovement(Vec3.ZERO);
        Vec3 attach = entity.position();
        if (mode == HookMode.ENTITY) {
            attach = attach.add(0.0D, entity.getBbHeight() * 0.5D, 0.0D);
        }
        setAttachPos(attach);
        setPos(attach);
    }

    @Nullable
    private Entity resolveHookedEntity() {
        if (hookedEntity != null && hookedEntity.isAlive()) {
            return hookedEntity;
        }
        int id = entityData.get(HOOKED_ENTITY_ID);
        if (id > 0) {
            Entity entity = level().getEntity(id);
            if (entity != null && entity.isAlive()) {
                hookedEntity = entity;
                return entity;
            }
        }
        return null;
    }

    private static boolean isGunInHand(Player player) {
        return player.getMainHandItem().getItem() instanceof PlungerGunItem
                || player.getOffhandItem().getItem() instanceof PlungerGunItem;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide) {
            Player owner = getGunOwner();
            if (owner != null) {
                PlungerGunItem.clearHookForEntity(owner, getUUID());
            }
        }
        super.remove(reason);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HookMode")) {
            setHookMode(HookMode.values()[tag.getInt("HookMode")]);
        }
        if (tag.contains("LatchMode")) {
            latchMode = HookMode.values()[tag.getInt("LatchMode")];
        }
        if (tag.hasUUID("HookedEntity")) {
            if (level() instanceof ServerLevel serverLevel) {
                hookedEntity = serverLevel.getEntity(tag.getUUID("HookedEntity"));
                if (hookedEntity != null) {
                    entityData.set(HOOKED_ENTITY_ID, hookedEntity.getId());
                }
            }
        }
        if (tag.contains("HookX")) {
            hookedPos = new BlockPos(tag.getInt("HookX"), tag.getInt("HookY"), tag.getInt("HookZ"));
        }
        if (tag.contains("AttachX")) {
            setAttachPos(new Vec3(tag.getDouble("AttachX"), tag.getDouble("AttachY"), tag.getDouble("AttachZ")));
        }
        life = tag.getInt("Life");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("HookMode", getHookMode().ordinal());
        tag.putInt("LatchMode", latchMode.ordinal());
        if (hookedEntity != null) {
            tag.putUUID("HookedEntity", hookedEntity.getUUID());
        }
        if (hookedPos != null) {
            tag.putInt("HookX", hookedPos.getX());
            tag.putInt("HookY", hookedPos.getY());
            tag.putInt("HookZ", hookedPos.getZ());
        }
        Vec3 attach = getAttachPos();
        tag.putDouble("AttachX", attach.x);
        tag.putDouble("AttachY", attach.y);
        tag.putDouble("AttachZ", attach.z);
        tag.putInt("Life", life);
    }

    public ItemStack getDisplayedItem() {
        return new ItemStack(RegistryAccess.item("plunger"));
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        if (getHookMode() == HookMode.FLYING) {
            super.move(type, movement);
        }
    }
}
