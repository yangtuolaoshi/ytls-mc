package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.CancellableGameEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public final class PlayerHurtEvent extends CancellableGameEvent {
    private final Player player;
    private final DamageSource source;
    private float amount;

    public PlayerHurtEvent(Player player, DamageSource source, float amount) {
        this.player = player;
        this.source = source;
        this.amount = amount;
    }

    public Player player() {
        return player;
    }

    public DamageSource source() {
        return source;
    }

    public float amount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
