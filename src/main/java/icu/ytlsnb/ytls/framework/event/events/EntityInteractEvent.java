package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.CancellableGameEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 玩家右键实体。在物品 {@code interactLivingEntity} 与实体自身交互之前触发；
 * 取消后可完全接管交互（例如避免村民打开交易界面）。
 */
public final class EntityInteractEvent extends CancellableGameEvent {
    private final Player player;
    private final Level level;
    private final ItemStack stack;
    private final InteractionHand hand;
    private final Entity target;

    private InteractionResult cancellationResult = InteractionResult.SUCCESS;

    public EntityInteractEvent(
            Player player,
            Level level,
            ItemStack stack,
            InteractionHand hand,
            Entity target) {
        this.player = player;
        this.level = level;
        this.stack = stack;
        this.hand = hand;
        this.target = target;
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

    public Entity target() {
        return target;
    }

    public InteractionResult cancellationResult() {
        return cancellationResult;
    }

    public void setCancellationResult(InteractionResult cancellationResult) {
        this.cancellationResult = cancellationResult;
    }
}
