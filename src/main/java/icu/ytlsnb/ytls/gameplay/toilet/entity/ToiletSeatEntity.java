package icu.ytlsnb.ytls.gameplay.toilet.entity;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ToiletSeatEntity extends Entity {
    private static final EntityDataAccessor<BlockPos> TOILET_POS =
            SynchedEntityData.defineId(ToiletSeatEntity.class, EntityDataSerializers.BLOCK_POS);

    public ToiletSeatEntity(EntityType<? extends ToiletSeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public ToiletSeatEntity(Level level, BlockPos toiletPos) {
        this(RegistryAccess.resolve(
                icu.ytlsnb.ytls.framework.registry.api.RegistryKind.ENTITY,
                "toilet_seat"
        ), level);
        setToiletPos(toiletPos);
        setPos(toiletPos.getX() + 0.5D, toiletPos.getY() + 0.35D, toiletPos.getZ() + 0.5D);
    }

    @Nullable
    public static ToiletSeatEntity create(Level level, BlockPos toiletPos) {
        ToiletSeatEntity seat = new ToiletSeatEntity(level, toiletPos);
        if (level.addFreshEntity(seat)) {
            return seat;
        }
        return null;
    }

    @Nullable
    public static ToiletSeatEntity findSeatAt(Level level, BlockPos toiletPos) {
        for (Entity entity : level.getEntitiesOfClass(ToiletSeatEntity.class,
                net.minecraft.world.phys.AABB.ofSize(
                        net.minecraft.world.phys.Vec3.atCenterOf(toiletPos), 0.5D, 0.5D, 0.5D))) {
            if (entity instanceof ToiletSeatEntity seat && seat.getToiletPos().equals(toiletPos)) {
                return seat;
            }
        }
        return null;
    }

    public BlockPos getToiletPos() {
        return entityData.get(TOILET_POS);
    }

    public void setToiletPos(BlockPos pos) {
        entityData.set(TOILET_POS, pos);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(TOILET_POS, BlockPos.ZERO);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("ToiletX")) {
            setToiletPos(new BlockPos(tag.getInt("ToiletX"), tag.getInt("ToiletY"), tag.getInt("ToiletZ")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        BlockPos pos = getToiletPos();
        tag.putInt("ToiletX", pos.getX());
        tag.putInt("ToiletY", pos.getY());
        tag.putInt("ToiletZ", pos.getZ());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getPassengers().isEmpty()) {
            discard();
        }
        BlockPos toiletPos = getToiletPos();
        setPos(toiletPos.getX() + 0.5D, toiletPos.getY() + 0.35D, toiletPos.getZ() + 0.5D);
    }

    @Override
    public void move(MoverType type, net.minecraft.world.phys.Vec3 delta) {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
