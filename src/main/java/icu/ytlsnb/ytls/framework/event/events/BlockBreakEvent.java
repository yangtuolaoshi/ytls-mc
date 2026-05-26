package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.CancellableGameEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockBreakEvent extends CancellableGameEvent {
    private final Level level;
    private final BlockPos pos;
    private final BlockState state;
    private final Player player;

    public BlockBreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.player = player;
    }

    public Level level() {
        return level;
    }

    public BlockPos pos() {
        return pos;
    }

    public BlockState state() {
        return state;
    }

    public Player player() {
        return player;
    }
}
