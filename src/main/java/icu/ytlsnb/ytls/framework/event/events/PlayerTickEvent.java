package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.GameEvent;
import net.minecraft.world.entity.player.Player;

public final class PlayerTickEvent extends GameEvent {
    private final Player player;

    public PlayerTickEvent(Player player) {
        this.player = player;
    }

    public Player player() {
        return player;
    }
}
