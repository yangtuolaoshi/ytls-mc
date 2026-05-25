package icu.ytlsnb.ytls.framework.event.bridge;

import icu.ytlsnb.ytls.framework.event.GameEventBus;
import icu.ytlsnb.ytls.framework.event.api.GameEventType;
import icu.ytlsnb.ytls.framework.event.events.PlayerLoginEvent;
import icu.ytlsnb.ytls.framework.event.events.PlayerLogoutEvent;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

/**
 * 将 Forge 原始事件桥接为框架语义事件。
 */
public final class ForgeEventBridge {
    private static final Logger LOG = FrameworkLog.of("event-bridge");

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
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            gameEventBus.post(GameEventType.PLAYER_LOGIN, new PlayerLoginEvent(serverPlayer));
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            gameEventBus.post(GameEventType.PLAYER_LOGOUT, new PlayerLogoutEvent(serverPlayer));
        }
    }
}
