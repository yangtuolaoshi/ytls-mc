package icu.ytlsnb.ytls.framework.skill;

import icu.ytlsnb.ytls.framework.component.api.ComponentTarget;
import icu.ytlsnb.ytls.framework.component.api.GameComponent;
import icu.ytlsnb.ytls.framework.component.annotation.RegisterComponent;
import icu.ytlsnb.ytls.framework.sync.annotation.SyncField;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

@RegisterComponent(value = "skill_state", targets = {ComponentTarget.ENTITY, ComponentTarget.PLAYER})
public final class SkillStateComponent implements GameComponent {
    @SyncField
    public String activeSkill = "";
    @SyncField
    public int castTicksRemaining;
    @SyncField
    public int totalCastTicks;
    public final Map<String, Integer> cooldowns = new HashMap<>();

    @Override
    public void save(CompoundTag tag) {
        tag.putString("activeSkill", activeSkill);
        tag.putInt("castTicksRemaining", castTicksRemaining);
        tag.putInt("totalCastTicks", totalCastTicks);
        CompoundTag cooldownTag = new CompoundTag();
        cooldowns.forEach((id, ticks) -> cooldownTag.putInt(id, ticks));
        tag.put("cooldowns", cooldownTag);
    }

    @Override
    public void load(CompoundTag tag) {
        activeSkill = tag.getString("activeSkill");
        castTicksRemaining = tag.getInt("castTicksRemaining");
        totalCastTicks = tag.getInt("totalCastTicks");
        cooldowns.clear();
        CompoundTag cooldownTag = tag.getCompound("cooldowns");
        for (String key : cooldownTag.getAllKeys()) {
            cooldowns.put(key, cooldownTag.getInt(key));
        }
    }

    @Override
    public void copyFrom(GameComponent other) {
        if (!(other instanceof SkillStateComponent skillState)) {
            return;
        }
        activeSkill = skillState.activeSkill;
        castTicksRemaining = skillState.castTicksRemaining;
        totalCastTicks = skillState.totalCastTicks;
        cooldowns.clear();
        cooldowns.putAll(skillState.cooldowns);
    }
}
