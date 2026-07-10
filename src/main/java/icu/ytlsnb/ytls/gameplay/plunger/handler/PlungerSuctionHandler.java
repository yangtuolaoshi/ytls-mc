package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public final class PlungerSuctionHandler {
    private static final String TAG_ENTITY = "SuckedEntity";

    private PlungerSuctionHandler() {
    }

    public static boolean hasSuckedEntity(ItemStack stack) {
        return stack.hasTag() && stack.getTag().hasUUID(TAG_ENTITY);
    }

    public static boolean toggleSuction(Player player, ItemStack stack, LivingEntity target) {
        if (hasSuckedEntity(stack)) {
            release(player, stack);
            return true;
        }
        if (target == null || !target.isAlive()) {
            return false;
        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.putUUID(TAG_ENTITY, target.getUUID());
        target.setNoGravity(true);
        target.setDeltaMovement(Vec3.ZERO);
        return true;
    }

    public static boolean launchSucked(Player player, ItemStack stack) {
        Entity entity = findSucked(player, stack);
        if (entity == null) {
            clear(stack);
            return false;
        }
        entity.setNoGravity(false);
        Vec3 look = player.getLookAngle().scale(2.5D);
        entity.setDeltaMovement(look);
        entity.hurtMarked = true;
        clear(stack);
        return true;
    }

    public static void tickPlayer(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        if (main.getItem() instanceof icu.ytlsnb.ytls.gameplay.plunger.item.PlungerItem && hasSuckedEntity(main)) {
            follow(player, main);
        }
        if (off.getItem() instanceof icu.ytlsnb.ytls.gameplay.plunger.item.PlungerItem && hasSuckedEntity(off)) {
            follow(player, off);
        }
    }

    private static void follow(Player player, ItemStack stack) {
        Entity entity = findSucked(player, stack);
        if (entity == null || !entity.isAlive()) {
            clear(stack);
            return;
        }
        Vec3 target = player.position().add(player.getLookAngle().scale(1.5D)).add(0.0D, 1.0D, 0.0D);
        Vec3 delta = target.subtract(entity.position()).scale(0.35D);
        entity.setDeltaMovement(delta);
        entity.setNoGravity(true);
        entity.hurtMarked = true;
    }

    private static void release(Player player, ItemStack stack) {
        Entity entity = findSucked(player, stack);
        if (entity != null) {
            entity.setNoGravity(false);
        }
        clear(stack);
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
        if (stack.hasTag()) {
            stack.getTag().remove(TAG_ENTITY);
        }
    }
}
