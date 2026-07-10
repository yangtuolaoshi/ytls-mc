package icu.ytlsnb.ytls.framework.event.bridge;

import icu.ytlsnb.ytls.framework.event.GameEventBus;
import icu.ytlsnb.ytls.framework.event.api.CancellableGameEvent;
import icu.ytlsnb.ytls.framework.event.api.GameEventType;
import icu.ytlsnb.ytls.framework.event.events.BlockBreakEvent;
import icu.ytlsnb.ytls.framework.event.events.BlockPlaceEvent;
import icu.ytlsnb.ytls.framework.event.events.ChunkLoadEvent;
import icu.ytlsnb.ytls.framework.event.events.DimensionChangeEvent;
import icu.ytlsnb.ytls.framework.event.events.EntityDeathEvent;
import icu.ytlsnb.ytls.framework.event.events.EntityInteractEvent;
import icu.ytlsnb.ytls.framework.event.events.EntitySpawnEvent;
import icu.ytlsnb.ytls.framework.event.events.ItemUseEvent;
import icu.ytlsnb.ytls.framework.event.events.PlayerDeathEvent;
import icu.ytlsnb.ytls.framework.event.events.PlayerHurtEvent;
import icu.ytlsnb.ytls.framework.event.events.LevelLoadEvent;
import icu.ytlsnb.ytls.framework.event.events.LevelUnloadEvent;
import icu.ytlsnb.ytls.framework.event.events.PlayerLoginEvent;
import icu.ytlsnb.ytls.framework.event.events.PlayerLogoutEvent;
import icu.ytlsnb.ytls.framework.event.events.RightClickBlockEvent;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

/**
 * 将 Forge 原始事件桥接为框架语义事件。
 */
public final class ForgeEventBridge {
    private static final Logger LOG = FrameworkLog.events();

    private final GameEventBus gameEventBus;

    public ForgeEventBridge(GameEventBus gameEventBus) {
        this.gameEventBus = gameEventBus;
    }

    public void register(IEventBus forgeBus) {
        forgeBus.register(this);
        LOG.info("Forge event bridge registered");
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            post(GameEventType.PLAYER_LOGIN, new PlayerLoginEvent(serverPlayer));
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            post(GameEventType.PLAYER_LOGOUT, new PlayerLogoutEvent(serverPlayer));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }
        FrameworkLog.eventDebug("Player tick: {}", event.player.getName().getString());
        gameEventBus.post(GameEventType.PLAYER_TICK, new icu.ytlsnb.ytls.framework.event.events.PlayerTickEvent(event.player));
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        PlayerHurtEvent gameEvent = new PlayerHurtEvent(player, event.getSource(), event.getAmount());
        post(GameEventType.PLAYER_HURT, gameEvent);
        if (gameEvent.isCancelled()) {
            event.setCanceled(true);
            return;
        }
        if (gameEvent.amount() != event.getAmount()) {
            event.setAmount(gameEvent.amount());
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            post(GameEventType.PLAYER_DEATH, new PlayerDeathEvent(player, event.getSource()));
        }
        post(GameEventType.ENTITY_DEATH, new EntityDeathEvent(entity, event.getSource()));
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        post(GameEventType.ENTITY_SPAWN, new EntitySpawnEvent(event.getEntity(), event.getLevel()));
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || !(event.getLevel() instanceof Level level)) {
            return;
        }
        BlockBreakEvent gameEvent = new BlockBreakEvent(level, event.getPos(), event.getState(), player);
        post(GameEventType.BLOCK_BREAK, gameEvent);
        if (gameEvent.isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getLevel() instanceof Level level)) {
            return;
        }
        BlockPlaceEvent gameEvent = new BlockPlaceEvent(level, event.getPos(), event.getPlacedBlock(), player);
        post(GameEventType.BLOCK_PLACE, gameEvent);
        if (gameEvent.isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            post(GameEventType.LEVEL_LOAD, new LevelLoadEvent(serverLevel));
        }
    }

    @SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            post(GameEventType.LEVEL_UNLOAD, new LevelUnloadEvent(serverLevel));
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        post(GameEventType.CHUNK_LOAD, new ChunkLoadEvent(serverLevel, event.getChunk().getPos()));
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        post(GameEventType.DIMENSION_CHANGE, new DimensionChangeEvent(
                serverPlayer, event.getFrom(), event.getTo()));
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickItem event) {
        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }
        ItemUseEvent gameEvent = new ItemUseEvent(
                event.getEntity(), level, event.getItemStack(), event.getHand());
        post(GameEventType.ITEM_USE, gameEvent);
        if (gameEvent.isCancelled()) {
            event.setCanceled(true);
            event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        // 服务端拦截开箱/睡觉；客户端也禁止方块 use，避免本地打开 GUI
        RightClickBlockEvent gameEvent = new RightClickBlockEvent(
                event.getEntity(),
                level,
                event.getItemStack(),
                event.getHand(),
                event.getPos(),
                level.getBlockState(event.getPos()),
                event.getHitVec());
        post(GameEventType.RIGHT_CLICK_BLOCK, gameEvent);
        if (gameEvent.denyBlockUse()) {
            event.setUseBlock(Event.Result.DENY);
        }
        if (gameEvent.forceItemUse()) {
            event.setUseItem(Event.Result.ALLOW);
        }
        if (gameEvent.isCancelled()) {
            event.setCanceled(true);
            event.setCancellationResult(gameEvent.cancellationResult());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        // 客户端也要派发，才能取消村民交易等本地 UI
        EntityInteractEvent gameEvent = new EntityInteractEvent(
                event.getEntity(),
                event.getLevel(),
                event.getItemStack(),
                event.getHand(),
                event.getTarget());
        post(GameEventType.ENTITY_INTERACT, gameEvent);
        if (gameEvent.isCancelled()) {
            event.setCanceled(true);
            event.setCancellationResult(gameEvent.cancellationResult());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        EntityInteractEvent gameEvent = new EntityInteractEvent(
                event.getEntity(),
                event.getLevel(),
                event.getItemStack(),
                event.getHand(),
                event.getTarget());
        post(GameEventType.ENTITY_INTERACT, gameEvent);
        if (gameEvent.isCancelled()) {
            event.setCanceled(true);
            event.setCancellationResult(gameEvent.cancellationResult());
        }
    }

    private void post(GameEventType type, icu.ytlsnb.ytls.framework.event.api.GameEvent event) {
        gameEventBus.post(type, event);
        if (event instanceof CancellableGameEvent cancellable) {
            FrameworkLog.eventDebug("Event {} cancelled={}", type, cancellable.isCancelled());
        }
    }
}
