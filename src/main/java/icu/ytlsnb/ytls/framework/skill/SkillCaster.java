package icu.ytlsnb.ytls.framework.skill;

import icu.ytlsnb.ytls.framework.component.ComponentAccess;
import icu.ytlsnb.ytls.framework.component.ComponentRegistry;
import icu.ytlsnb.ytls.framework.component.api.ComponentType;
import icu.ytlsnb.ytls.framework.skill.api.Skill;
import icu.ytlsnb.ytls.framework.skill.api.SkillContext;
import icu.ytlsnb.ytls.framework.sync.SyncHelper;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

import java.util.Map;

/**
 * 实体技能施法与冷却管理。
 */
public final class SkillCaster {
    private static final Logger LOG = FrameworkLog.of("skill");

    private SkillCaster() {
    }

    public static ComponentType<SkillStateComponent> stateType() {
        return ComponentRegistry.type("skill_state");
    }

    public static boolean tryCast(LivingEntity caster, String skillId) {
        Skill skill = SkillRegistry.require(skillId);
        SkillStateComponent state = stateOf(caster);
        SkillContext context = new SkillContext(caster);

        if (state.activeSkill != null && !state.activeSkill.isEmpty()) {
            return false;
        }
        if (state.cooldowns.getOrDefault(skillId, 0) > 0) {
            return false;
        }
        if (!skill.canCast(context)) {
            return false;
        }

        state.activeSkill = skillId;
        state.castTicksRemaining = skill.castTicks();
        state.totalCastTicks = skill.castTicks();
        skill.onCastStart(context);
        SyncHelper.markDirty(caster, state);
        LOG.debug("Skill cast started: {} by {}", skillId, caster.getName().getString());
        return true;
    }

    public static void tick(LivingEntity entity) {
        SkillStateComponent state = stateOf(entity);
        tickCooldowns(state);

        if (state.activeSkill == null || state.activeSkill.isEmpty()) {
            return;
        }

        Skill skill = SkillRegistry.require(state.activeSkill);
        SkillContext context = new SkillContext(entity);
        skill.onCastTick(context);

        state.castTicksRemaining--;
        if (state.castTicksRemaining <= 0) {
            skill.onCastEnd(context);
            state.cooldowns.put(state.activeSkill, skill.cooldownTicks());
            state.activeSkill = "";
            state.castTicksRemaining = 0;
            state.totalCastTicks = 0;
            LOG.debug("Skill cast finished: {}", skill.id());
        }
        SyncHelper.markDirty(entity, state);
    }

    public static SkillStateComponent stateOf(LivingEntity entity) {
        return ComponentAccess.getOrCreate(entity, stateType());
    }

    private static void tickCooldowns(SkillStateComponent state) {
        state.cooldowns.replaceAll((id, ticks) -> Math.max(0, ticks - 1));
    }
}
