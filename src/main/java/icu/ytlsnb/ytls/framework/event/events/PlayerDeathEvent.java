package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.GameEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public final class PlayerDeathEvent extends GameEvent {
    private final Player player;
    private final DamageSource source;

    public PlayerDeathEvent(Player player, DamageSource source) {
        this.player = player;
        this.source = source;
    }

    public Player player() {
        return player;
    }

    public DamageSource source() {
        return source;
    }
}
