package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.CancellableGameEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 玩家右键方块。可取消整次交互，也可单独禁止方块 {@code use()} / 强制走物品 {@code useOn()}。
 */
public final class RightClickBlockEvent extends CancellableGameEvent {
    private final Player player;
    private final Level level;
    private final ItemStack stack;
    private final InteractionHand hand;
    private final BlockPos pos;
    private final BlockState state;
    private final BlockHitResult hitResult;

    private boolean denyBlockUse;
    private boolean forceItemUse;
    private InteractionResult cancellationResult = InteractionResult.SUCCESS;

    public RightClickBlockEvent(
            Player player,
            Level level,
            ItemStack stack,
            InteractionHand hand,
            BlockPos pos,
            BlockState state,
            BlockHitResult hitResult) {
        this.player = player;
        this.level = level;
        this.stack = stack;
        this.hand = hand;
        this.pos = pos;
        this.state = state;
        this.hitResult = hitResult;
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

    public BlockPos pos() {
        return pos;
    }

    public BlockState state() {
        return state;
    }

    public BlockHitResult hitResult() {
        return hitResult;
    }

    public boolean denyBlockUse() {
        return denyBlockUse;
    }

    public void setDenyBlockUse(boolean denyBlockUse) {
        this.denyBlockUse = denyBlockUse;
    }

    public boolean forceItemUse() {
        return forceItemUse;
    }

    public void setForceItemUse(boolean forceItemUse) {
        this.forceItemUse = forceItemUse;
    }

    public InteractionResult cancellationResult() {
        return cancellationResult;
    }

    public void setCancellationResult(InteractionResult cancellationResult) {
        this.cancellationResult = cancellationResult;
    }
}
