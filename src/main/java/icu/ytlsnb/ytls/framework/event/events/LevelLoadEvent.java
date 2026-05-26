package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.GameEvent;
import net.minecraft.server.level.ServerLevel;

public final class LevelLoadEvent extends GameEvent {
    private final ServerLevel level;

    public LevelLoadEvent(ServerLevel level) {
        this.level = level;
    }

    public ServerLevel level() {
        return level;
    }
}
