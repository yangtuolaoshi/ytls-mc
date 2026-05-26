package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.GameEvent;
import net.minecraftforge.fml.config.ModConfig;

public final class ConfigReloadEvent extends GameEvent {
    private final ModConfig config;

    public ConfigReloadEvent(ModConfig config) {
        this.config = config;
    }

    public ModConfig config() {
        return config;
    }
}
