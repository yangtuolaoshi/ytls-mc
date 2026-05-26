package icu.ytlsnb.ytls.framework.bootstrap;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.event.api.GameEventType;
import icu.ytlsnb.ytls.framework.event.events.ConfigReloadEvent;
import icu.ytlsnb.ytls.framework.event.events.ResourceReloadEvent;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

/**
 * 配置与资源重载桥接：触发语义事件与生命周期回调。
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public final class FrameworkReloadBridge {
    private static final Logger LOG = FrameworkLog.of("reload");

    private FrameworkReloadBridge() {
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (!Framework.isLoaded()) {
            return;
        }
        ModConfig config = event.getConfig();
        LOG.info("Config reloaded: {}", config.getFileName());
        Framework.get().events().post(GameEventType.CONFIG_RELOAD, new ConfigReloadEvent(config));
        Framework.get().lifecycle().fire(LifecyclePhase.CONFIG_RELOAD, config);
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (!Framework.isLoaded()) {
            return;
        }
        if (event.getUpdateCause() != TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            return;
        }
        LOG.info("Server data reloaded (tags/resources)");
        Framework.get().events().post(GameEventType.RESOURCE_RELOAD, new ResourceReloadEvent());
        Framework.get().lifecycle().fire(LifecyclePhase.RESOURCE_RELOAD, event);
    }
}
