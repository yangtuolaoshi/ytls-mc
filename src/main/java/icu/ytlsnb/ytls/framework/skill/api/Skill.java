package icu.ytlsnb.ytls.framework.skill.api;

import net.minecraft.resources.ResourceLocation;

/**
 * 技能抽象。服务端负责判定，客户端表现通过 PresentationEmitter 触发。
 */
public interface Skill {
    ResourceLocation id();

    int cooldownTicks();

    int castTicks();

    boolean canCast(SkillContext context);

    void onCastStart(SkillContext context);

    void onCastTick(SkillContext context);

    void onCastEnd(SkillContext context);
}
