package icu.ytlsnb.ytls.framework.event.events;

import icu.ytlsnb.ytls.framework.event.api.CancellableGameEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class EntitySpawnEvent extends CancellableGameEvent {
    private final Entity entity;
    private final Level level;

    public EntitySpawnEvent(Entity entity, Level level) {
        this.entity = entity;
        this.level = level;
    }

    public Entity entity() {
        return entity;
    }

    public Level level() {
        return level;
    }
}
