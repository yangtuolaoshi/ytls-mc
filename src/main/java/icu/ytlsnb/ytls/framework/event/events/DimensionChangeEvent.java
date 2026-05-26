package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.GameEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public final class DimensionChangeEvent extends GameEvent {
    private final ServerPlayer player;
    private final ResourceKey<Level> from;
    private final ResourceKey<Level> to;

    public DimensionChangeEvent(ServerPlayer player, ResourceKey<Level> from, ResourceKey<Level> to) {
        this.player = player;
        this.from = from;
        this.to = to;
    }

    public ServerPlayer player() {
        return player;
    }

    public ResourceKey<Level> from() {
        return from;
    }

    public ResourceKey<Level> to() {
        return to;
    }
}
