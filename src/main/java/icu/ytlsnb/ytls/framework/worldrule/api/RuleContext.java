package icu.ytlsnb.ytls.framework.worldrule.api;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class RuleContext {
    private final ServerLevel level;
    private final long gameTime;
    private final BlockPos origin;

    public RuleContext(ServerLevel level) {
        this(level, BlockPos.ZERO);
    }

    public RuleContext(ServerLevel level, BlockPos origin) {
        this.level = level;
        this.gameTime = level.getGameTime();
        this.origin = origin;
    }

    public ServerLevel level() {
        return level;
    }

    public long gameTime() {
        return gameTime;
    }

    public BlockPos origin() {
        return origin;
    }

    public ResourceKey<Level> dimension() {
        return level.dimension();
    }
}
