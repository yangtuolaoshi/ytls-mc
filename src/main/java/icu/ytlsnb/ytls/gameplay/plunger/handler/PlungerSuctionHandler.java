package icu.ytlsnb.ytls.gameplay.plunger.handler;

import icu.ytlsnb.ytls.gameplay.plunger.item.PlungerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shift+右键吸附实体：服务端追踪表 + 每 tick 强制传送同步，避免只悬空不跟随。
 */
public final class PlungerSuctionHandler {
    private static final String TAG_ENTITY = "SuckedEntity";
    private static final String TAG_OFF_X = "SuckOffX";
    private static final String TAG_OFF_Y = "SuckOffY";
    private static final String TAG_OFF_Z = "SuckOffZ";
    private static final String TAG_NO_AI = "SuckHadNoAi";
    private static final String TAG_NO_PHYSICS = "SuckHadNoPhysics";
    /** 玩家与被吸生物碰撞箱之间的额外空隙，避免互相挤压 */
    private static final double HOLD_GAP = 0.5D;
    /** 吸附高度（相对玩家脚底） */
    private static final double HOLD_HEIGHT = 1.05D;
    private static final double HOLD_DIST_MIN = 1.0D;
    private static final double HOLD_DIST_MAX = 2.2D;

    /** playerUUID -> 吸附状态（权威数据在服务端内存，NBT 作持久化备份） */
    private static final Map<UUID, CaptureState> CAPTURES = new ConcurrentHashMap<>();

    private PlungerSuctionHandler() {
    }

    public static boolean hasSuckedEntity(ItemStack stack) {
        return stack.hasTag() && stack.getTag().hasUUID(TAG_ENTITY);
    }

    public static boolean tryCapture(Player player, ItemStack stack, LivingEntity target) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        if (hasSuckedEntity(stack) || CAPTURES.containsKey(player.getUUID())
                || target == null || !target.isAlive() || target instanceof Player) {
            return false;
        }

        // 按双方碰撞箱留出空隙，避免贴太近把玩家挤开
        Vec3 offset = holdOffset(player, target);

        CompoundTag tag = stack.getOrCreateTag();
        tag.putUUID(TAG_ENTITY, target.getUUID());
        tag.putDouble(TAG_OFF_X, offset.x);
        tag.putDouble(TAG_OFF_Y, offset.y);
        tag.putDouble(TAG_OFF_Z, offset.z);

        boolean hadNoAi = false;
        if (target instanceof Mob mob) {
            hadNoAi = mob.isNoAi();
            tag.putBoolean(TAG_NO_AI, hadNoAi);
            mob.setNoAi(true);
        }
        tag.putBoolean(TAG_NO_PHYSICS, target.noPhysics);
        target.noPhysics = true;
        target.setNoGravity(true);
        target.setDeltaMovement(Vec3.ZERO);
        target.hurtMarked = true;

        CAPTURES.put(player.getUUID(), new CaptureState(
                target.getUUID(),
                target.getId(),
                offset,
                hadNoAi,
                tag.getBoolean(TAG_NO_PHYSICS),
                serverLevel.dimension()));

        snapToOffset(serverLevel, player, target, offset);
        return true;
    }

    public static boolean launchSucked(Player player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        Entity entity = findSucked(player, stack);
        if (entity == null) {
            clearAll(player, stack);
            return false;
        }
        restoreEntity(entity, stack, player.getUUID());
        Vec3 look = player.getLookAngle().scale(2.8D);
        entity.setDeltaMovement(look);
        entity.hurtMarked = true;
        entity.hasImpulse = true;
        clearAll(player, stack);
        return true;
    }

    public static void tickPlayer(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack plunger = findPlungerWithCapture(player);
        CaptureState state = CAPTURES.get(player.getUUID());

        // 内存状态丢失时，尝试从物品 NBT 恢复
        if (state == null && plunger != null && hasSuckedEntity(plunger)) {
            state = restoreStateFromItem(player, plunger, serverLevel);
        }
        if (state == null) {
            return;
        }
        // 马桶塞不在背包了：释放实体
        if (plunger == null) {
            Entity entity = resolveEntity(serverLevel, state);
            if (entity != null) {
                restoreEntity(entity, null, player.getUUID());
            }
            CAPTURES.remove(player.getUUID());
            return;
        }

        follow(serverLevel, player, plunger, state);
    }

    private static void follow(ServerLevel level, Player player, ItemStack stack, CaptureState state) {
        Entity entity = resolveEntity(level, state);
        if (entity == null || !entity.isAlive()) {
            if (entity != null) {
                restoreEntity(entity, stack, player.getUUID());
            }
            clearAll(player, stack);
            return;
        }
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
        }
        entity.noPhysics = true;
        entity.setNoGravity(true);
        // 每 tick 按玩家朝向 + 碰撞箱安全距离跟随
        Vec3 offset = holdOffset(player, entity);
        CAPTURES.put(player.getUUID(), new CaptureState(
                state.entityUuid, state.networkId, offset, state.hadNoAi, state.hadNoPhysics, state.dimension));
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            tag.putDouble(TAG_OFF_X, offset.x);
            tag.putDouble(TAG_OFF_Y, offset.y);
            tag.putDouble(TAG_OFF_Z, offset.z);
        }
        snapToOffset(level, player, entity, offset);
    }

    /**
     * 身前略抬高携带；水平距离 = 双方半宽 + 空隙，避免碰撞箱重叠把玩家挤开。
     */
    private static Vec3 holdOffset(Player player, Entity held) {
        Vec3 look = player.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0.0D, look.z);
        if (flat.lengthSqr() < 1.0E-6D) {
            float yaw = player.getYRot() * ((float) Math.PI / 180.0F);
            flat = new Vec3(-Math.sin(yaw), 0.0D, Math.cos(yaw));
        }
        flat = flat.normalize();
        double dist = player.getBbWidth() * 0.5D + held.getBbWidth() * 0.5D + HOLD_GAP;
        dist = Math.max(HOLD_DIST_MIN, Math.min(dist, HOLD_DIST_MAX));
        return flat.scale(dist).add(0.0D, HOLD_HEIGHT, 0.0D);
    }

    private static void snapToOffset(ServerLevel level, Player player, Entity entity, Vec3 offset) {
        Vec3 target = player.position().add(offset);
        entity.moveTo(target.x, target.y, target.z, entity.getYRot(), entity.getXRot());
        entity.setPos(target.x, target.y, target.z);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.hasImpulse = true;
        entity.hurtMarked = true;
        entity.fallDistance = 0.0F;
        // 强制客户端传送同步，避免只在服务端跟着走、客户端仍悬空原地
        level.getChunkSource().broadcastAndSend(entity, new ClientboundTeleportEntityPacket(entity));
    }

    private static void restoreEntity(Entity entity, ItemStack stack, UUID playerId) {
        CaptureState state = CAPTURES.get(playerId);
        boolean hadNoPhysics = state != null
                ? state.hadNoPhysics
                : stack != null && stack.hasTag() && stack.getTag().getBoolean(TAG_NO_PHYSICS);
        boolean hadNoAi = state != null
                ? state.hadNoAi
                : stack != null && stack.hasTag() && stack.getTag().getBoolean(TAG_NO_AI);

        entity.noPhysics = hadNoPhysics;
        entity.setNoGravity(false);
        if (entity instanceof Mob mob) {
            mob.setNoAi(hadNoAi);
        }
    }

    private static Entity findSucked(Player player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        CaptureState state = CAPTURES.get(player.getUUID());
        if (state != null) {
            Entity entity = resolveEntity(serverLevel, state);
            if (entity != null) {
                return entity;
            }
        }
        if (!hasSuckedEntity(stack)) {
            return null;
        }
        return serverLevel.getEntity(stack.getTag().getUUID(TAG_ENTITY));
    }

    private static Entity resolveEntity(ServerLevel level, CaptureState state) {
        Entity byId = level.getEntity(state.networkId);
        if (byId != null && byId.getUUID().equals(state.entityUuid)) {
            return byId;
        }
        return level.getEntity(state.entityUuid);
    }

    private static ItemStack findPlungerWithCapture(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof PlungerItem && hasSuckedEntity(stack)) {
                return stack;
            }
        }
        // 内存有捕获但 NBT 可能尚未写上时，仍返回手上的马桶塞
        if (CAPTURES.containsKey(player.getUUID())) {
            ItemStack main = player.getMainHandItem();
            if (main.getItem() instanceof PlungerItem) {
                return main;
            }
            ItemStack off = player.getOffhandItem();
            if (off.getItem() instanceof PlungerItem) {
                return off;
            }
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() instanceof PlungerItem) {
                    return stack;
                }
            }
        }
        return null;
    }

    private static CaptureState restoreStateFromItem(Player player, ItemStack stack, ServerLevel level) {
        if (!hasSuckedEntity(stack)) {
            return null;
        }
        CompoundTag tag = stack.getTag();
        UUID entityId = tag.getUUID(TAG_ENTITY);
        Entity entity = level.getEntity(entityId);
        if (entity == null) {
            clear(stack);
            return null;
        }
        Vec3 offset = new Vec3(tag.getDouble(TAG_OFF_X), tag.getDouble(TAG_OFF_Y), tag.getDouble(TAG_OFF_Z));
        CaptureState state = new CaptureState(
                entityId,
                entity.getId(),
                offset,
                tag.getBoolean(TAG_NO_AI),
                tag.getBoolean(TAG_NO_PHYSICS),
                level.dimension());
        CAPTURES.put(player.getUUID(), state);
        return state;
    }

    private static void clearAll(Player player, ItemStack stack) {
        CAPTURES.remove(player.getUUID());
        clear(stack);
    }

    private static void clear(ItemStack stack) {
        if (stack == null || !stack.hasTag()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        tag.remove(TAG_ENTITY);
        tag.remove(TAG_OFF_X);
        tag.remove(TAG_OFF_Y);
        tag.remove(TAG_OFF_Z);
        tag.remove(TAG_NO_AI);
        tag.remove(TAG_NO_PHYSICS);
        if (tag.isEmpty()) {
            stack.setTag(null);
        }
    }

    /** 玩家退出时清理，避免泄漏 */
    public static void clearPlayer(UUID playerId) {
        CAPTURES.remove(playerId);
    }

    public static void clearDimension(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension) {
        Iterator<Map.Entry<UUID, CaptureState>> it = CAPTURES.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().dimension.equals(dimension)) {
                it.remove();
            }
        }
    }

    private record CaptureState(
            UUID entityUuid,
            int networkId,
            Vec3 offset,
            boolean hadNoAi,
            boolean hadNoPhysics,
            net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension) {
    }
}
