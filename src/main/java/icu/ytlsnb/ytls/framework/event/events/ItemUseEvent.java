package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.CancellableGameEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ItemUseEvent extends CancellableGameEvent {
    private final Player player;
    private final Level level;
    private final ItemStack stack;
    private final InteractionHand hand;

    public ItemUseEvent(Player player, Level level, ItemStack stack, InteractionHand hand) {
        this.player = player;
        this.level = level;
        this.stack = stack;
        this.hand = hand;
    }

    public Player player() {
        return player;
    }

    public Level level() {
        return level;
    }

    public ItemStack stack() {
        return stack;
    }

    public InteractionHand hand() {
        return hand;
    }
}
