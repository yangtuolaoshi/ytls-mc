package icu.ytlsnb.ytls.gameplay.plunger.entity;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.gameplay.plunger.item.PlungerGunItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PlungerHookEntity extends Projectile {
    public enum HookMode {
        FLYING,
        ENTITY,
        ITEM,
        BLOCK,
        REELING
    }

    private static final EntityDataAccessor<Integer> HOOK_MODE =
            SynchedEntityData.defineId(PlungerHookEntity.class, EntityDataSerializers.INT);

    private static final int MAX_FLIGHT_TICKS = 200;
    private static final double REEL_SPEED = 2.8D;

    private int noCollisionTicks;
    @Nullable
    private Entity hookedEntity;
    @Nullable
    private BlockPos hookedPos;

    public PlungerHookEntity(EntityType<? extends PlungerHookEntity> type, Level level) {
        super(type, level);
    }

    public PlungerHookEntity(Level level, Player owner) {
        this(RegistryAccess.resolve(
                icu.ytlsnb.ytls.framework.registry.api.RegistryKind.ENTITY,
                "plunger_hook"
        ), level);
        setOwner(owner);
    }

    public void shootFromPlayer(Player player, float power) {
        Vec3 eye = player.getEyePosition();
        moveTo(eye.x, eye.y - 0.1D, eye.z, player.getYRot(), player.getXRot());
        Vec3 look = player.getLookAngle();
        float velocity = 1.2F + power * 2.0F;
        shoot(look.x, look.y, look.z, velocity, 0.0F);
        setHookMode(HookMode.FLYING);
        noCollisionTicks = 5;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(HOOK_MODE, HookMode.FLYING.ordinal());
    }

    public HookMode getHookMode() {
        return HookMode.values()[entityData.get(HOOK_MODE)];
    }

    public void setHookMode(HookMode mode) {
        entityData.set(HOOK_MODE, mode.ordinal());
    }

    @Nullable
    public Entity getHookedEntity() {
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
    public boolean isNoGravity() {
        HookMode mode = getHookMode();
        return mode == HookMode.FLYING || mode == HookMode.REELING;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return noCollisionTicks <= 0 && super.canHitEntity(entity) && entity != getOwner();
    }

    @Override
    public void tick() {
        super.tick();

        Player owner = getGunOwner();
        if (!level().isClientSide && (owner == null || !owner.isAlive())) {
            discard();
            return;
        }

        if (noCollisionTicks > 0) {
            noCollisionTicks--;
        }

        switch (getHookMode()) {
            case FLYING -> {
                if (!level().isClientSide && tickCount > MAX_FLIGHT_TICKS) {
                    discard();
                }
            }
            case REELING -> reelTowardOwner();
            case ENTITY, ITEM -> followHookedEntity();
            case BLOCK -> {
                if (hookedPos != null) {
                    setPos(Vec3.atCenterOf(hookedPos));
                }
            }
        }
    }

    private void followHookedEntity() {
        if (hookedEntity == null || !hookedEntity.isAlive()) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }
        Vec3 attach = hookedEntity.position();
        if (getHookMode() == HookMode.ENTITY) {
            attach = attach.add(0.0D, hookedEntity.getBbHeight() * 0.5D, 0.0D);
        }
        setPos(attach);
        setDeltaMovement(Vec3.ZERO);
    }

    private void reelTowardOwner() {
        Player owner = getGunOwner();
        if (owner == null) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }
        Vec3 target = owner.getEyePosition().add(owner.getLookAngle().scale(0.5D));
        Vec3 delta = target.subtract(position());
        double distance = delta.length();
        if (distance < 1.2D) {
            if (!level().isClientSide) {
                finishReel(owner);
            }
            return;
        }
        Vec3 motion = delta.normalize().scale(Math.min(REEL_SPEED, distance * 0.6D));
        setDeltaMovement(motion);
        setPos(position().add(motion));
    }

    public void startReelIn() {
        if (level().isClientSide) {
            return;
        }
        setHookMode(HookMode.REELING);
        setDeltaMovement(Vec3.ZERO);
        noCollisionTicks = 0;
    }

    private void finishReel(Player player) {
        if (hookedEntity != null && hookedEntity.isAlive()) {
            pullEntityToward(player);
        } else if (hookedPos != null) {
            pullPlayerToward(player);
        }
        PlungerGunItem.clearHookForEntity(player, getUUID());
        discard();
    }

    @Override
    protected void onHit(HitResult result) {
        if (noCollisionTicks > 0) {
            return;
        }
        super.onHit(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof ItemEntity itemEntity) {
            hookedEntity = itemEntity;
            setHookMode(HookMode.ITEM);
        } else if (entity != getOwner()) {
            hookedEntity = entity;
            setHookMode(HookMode.ENTITY);
        }
        setDeltaMovement(Vec3.ZERO);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        hookedPos = result.getBlockPos();
        setHookMode(HookMode.BLOCK);
        setDeltaMovement(Vec3.ZERO);
    }

    private void pullEntityToward(Player player) {
        if (hookedEntity == null) {
            return;
        }
        Vec3 toPlayer = player.position().subtract(hookedEntity.position()).normalize().scale(2.0D);
        hookedEntity.setDeltaMovement(toPlayer);
        hookedEntity.hurtMarked = true;
        hookedEntity.setNoGravity(false);
    }

    private void pullPlayerToward(Player player) {
        if (hookedPos == null) {
            return;
        }
        Vec3 toBlock = Vec3.atCenterOf(hookedPos).subtract(player.position()).normalize().scale(2.0D);
        player.setDeltaMovement(toBlock);
        player.hurtMarked = true;
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
        if (tag.contains("HookMode")) {
            setHookMode(HookMode.values()[tag.getInt("HookMode")]);
        }
        if (tag.hasUUID("HookedEntity")) {
            if (level() instanceof ServerLevel serverLevel) {
                hookedEntity = serverLevel.getEntity(tag.getUUID("HookedEntity"));
            }
        }
        if (tag.contains("HookX")) {
            hookedPos = new BlockPos(tag.getInt("HookX"), tag.getInt("HookY"), tag.getInt("HookZ"));
        }
        noCollisionTicks = tag.getInt("NoCollisionTicks");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("HookMode", getHookMode().ordinal());
        if (hookedEntity != null) {
            tag.putUUID("HookedEntity", hookedEntity.getUUID());
        }
        if (hookedPos != null) {
            tag.putInt("HookX", hookedPos.getX());
            tag.putInt("HookY", hookedPos.getY());
            tag.putInt("HookZ", hookedPos.getZ());
        }
        tag.putInt("NoCollisionTicks", noCollisionTicks);
    }

    public ItemStack getDisplayedItem() {
        return new ItemStack(RegistryAccess.item("plunger"));
    }
}
