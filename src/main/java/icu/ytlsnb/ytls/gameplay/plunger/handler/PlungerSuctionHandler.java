package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public final class PlungerSuctionHandler {
    private static final String TAG_ENTITY = "SuckedEntity";
    private static final String TAG_OFF_X = "SuckOffX";
    private static final String TAG_OFF_Y = "SuckOffY";
    private static final String TAG_OFF_Z = "SuckOffZ";
    private static final String TAG_NO_AI = "SuckHadNoAi";
    private static final double MAX_OFFSET = 3.0D;

    private PlungerSuctionHandler() {
    }

    public static boolean hasSuckedEntity(ItemStack stack) {
        return stack.hasTag() && stack.getTag().hasUUID(TAG_ENTITY);
    }

    public static boolean tryCapture(Player player, ItemStack stack, LivingEntity target) {
        if (hasSuckedEntity(stack) || target == null || !target.isAlive() || target instanceof Player) {
            return false;
        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.putUUID(TAG_ENTITY, target.getUUID());

        Vec3 offset = target.position().subtract(player.position());
        if (offset.lengthSqr() > MAX_OFFSET * MAX_OFFSET) {
            offset = offset.normalize().scale(MAX_OFFSET);
        }
        // 略抬高，悬空跟随
        if (offset.y < 0.8D) {
            offset = new Vec3(offset.x, 0.8D, offset.z);
        }
        tag.putDouble(TAG_OFF_X, offset.x);
        tag.putDouble(TAG_OFF_Y, offset.y);
        tag.putDouble(TAG_OFF_Z, offset.z);

        if (target instanceof Mob mob) {
            tag.putBoolean(TAG_NO_AI, mob.isNoAi());
            mob.setNoAi(true);
        }
        target.setNoGravity(true);
        target.setDeltaMovement(Vec3.ZERO);
        target.hurtMarked = true;
        snapToOffset(player, target, offset);
        return true;
    }

    public static boolean launchSucked(Player player, ItemStack stack) {
        Entity entity = findSucked(player, stack);
        if (entity == null) {
            clear(stack);
            return false;
        }
        restoreEntity(entity, stack);
        Vec3 look = player.getLookAngle().scale(2.8D);
        entity.setDeltaMovement(look);
        entity.hurtMarked = true;
        entity.hasImpulse = true;
        clear(stack);
        return true;
    }

    public static void tickPlayer(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof icu.ytlsnb.ytls.gameplay.plunger.item.PlungerItem
                    && hasSuckedEntity(stack)) {
                follow(player, stack);
            }
        }
    }

    private static void follow(Player player, ItemStack stack) {
        Entity entity = findSucked(player, stack);
        if (entity == null || !entity.isAlive()) {
            if (entity != null) {
                restoreEntity(entity, stack);
            }
            clear(stack);
            return;
        }
        CompoundTag tag = stack.getTag();
        Vec3 offset = new Vec3(tag.getDouble(TAG_OFF_X), tag.getDouble(TAG_OFF_Y), tag.getDouble(TAG_OFF_Z));
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
        }
        entity.setNoGravity(true);
        snapToOffset(player, entity, offset);
    }

    private static void snapToOffset(Player player, Entity entity, Vec3 offset) {
        Vec3 target = player.position().add(offset);
        entity.moveTo(target.x, target.y, target.z, entity.getYRot(), entity.getXRot());
        entity.setDeltaMovement(Vec3.ZERO);
        entity.hurtMarked = true;
        entity.hasImpulse = true;
        entity.fallDistance = 0.0F;
    }

    private static void restoreEntity(Entity entity, ItemStack stack) {
        entity.setNoGravity(false);
        if (entity instanceof Mob mob && stack.hasTag()) {
            boolean hadNoAi = stack.getTag().getBoolean(TAG_NO_AI);
            mob.setNoAi(hadNoAi);
        }
    }

    private static Entity findSucked(Player player, ItemStack stack) {
        if (!hasSuckedEntity(stack)) {
            return null;
        }
        UUID id = stack.getTag().getUUID(TAG_ENTITY);
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getEntity(id);
    }

    private static void clear(ItemStack stack) {
        if (!stack.hasTag()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        tag.remove(TAG_ENTITY);
        tag.remove(TAG_OFF_X);
        tag.remove(TAG_OFF_Y);
        tag.remove(TAG_OFF_Z);
        tag.remove(TAG_NO_AI);
        if (tag.isEmpty()) {
            stack.setTag(null);
        }
    }
}
