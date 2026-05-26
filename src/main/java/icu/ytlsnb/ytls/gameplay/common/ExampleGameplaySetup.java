package icu.ytlsnb.ytls.gameplay.common;

import icu.ytlsnb.ytls.framework.bootstrap.LifecyclePhase;
import icu.ytlsnb.ytls.framework.bootstrap.annotation.OnLifecycle;
import icu.ytlsnb.ytls.framework.event.annotation.Listen;
import icu.ytlsnb.ytls.framework.event.api.GameEventType;
import icu.ytlsnb.ytls.framework.event.events.LevelLoadEvent;
import icu.ytlsnb.ytls.framework.event.events.PlayerLoginEvent;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;

/**
 * 示例业务接入：生命周期、事件、网络。
 */
public final class ExampleGameplaySetup {
    private static final Logger LOG = FrameworkLog.of("gameplay");

    private ExampleGameplaySetup() {
    }

    @OnLifecycle(LifecyclePhase.COMMON_SETUP)
    public static void onCommonSetup() {
        LOG.info("Example gameplay common setup complete");
    }

    @Listen(GameEventType.PLAYER_LOGIN)
    public static void onPlayerLogin(PlayerLoginEvent event) {
        LOG.info("Player login: {}", event.player().getName().getString());
    }

    @OnLifecycle(LifecyclePhase.WORLD_LOAD)
    public static void onWorldLoad(ServerLevel level) {
        LOG.info("World loaded: {}", level.dimension().location());
    }

    @Listen(GameEventType.LEVEL_LOAD)
    public static void onLevelLoadEvent(LevelLoadEvent event) {
        LOG.debug("Level load event: {}", event.level().dimension().location());
    }
}
