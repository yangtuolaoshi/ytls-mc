package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.GameEvent;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerLogoutEvent extends GameEvent {
    private final ServerPlayer player;

    public PlayerLogoutEvent(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer player() {
        return player;
    }
}
