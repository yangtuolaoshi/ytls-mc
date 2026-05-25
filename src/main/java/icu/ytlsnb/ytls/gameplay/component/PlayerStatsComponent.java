package icu.ytlsnb.ytls.gameplay.component;

import icu.ytlsnb.ytls.framework.component.ComponentRegistry;
import icu.ytlsnb.ytls.framework.component.api.ComponentTarget;
import icu.ytlsnb.ytls.framework.component.api.ComponentType;
import icu.ytlsnb.ytls.framework.component.api.GameComponent;
import icu.ytlsnb.ytls.framework.component.annotation.RegisterComponent;
import icu.ytlsnb.ytls.framework.sync.annotation.SyncField;
import net.minecraft.nbt.CompoundTag;

@RegisterComponent(value = "player_stats", targets = {ComponentTarget.PLAYER})
public final class PlayerStatsComponent implements GameComponent {
    public static ComponentType<PlayerStatsComponent> type() {
        return ComponentRegistry.type("player_stats");
    }

    @SyncField
    public int exampleValue = 0;

    @Override
    public void save(CompoundTag tag) {
        tag.putInt("exampleValue", exampleValue);
    }

    @Override
    public void load(CompoundTag tag) {
        exampleValue = tag.getInt("exampleValue");
    }

    @Override
    public void copyFrom(GameComponent other) {
        if (other instanceof PlayerStatsComponent stats) {
            exampleValue = stats.exampleValue;
        }
    }
}
