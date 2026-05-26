package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.GameEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class ChunkLoadEvent extends GameEvent {
    private final ServerLevel level;
    private final ChunkPos chunkPos;

    public ChunkLoadEvent(ServerLevel level, ChunkPos chunkPos) {
        this.level = level;
        this.chunkPos = chunkPos;
    }

    public ServerLevel level() {
        return level;
    }

    public ChunkPos chunkPos() {
        return chunkPos;
    }
}
