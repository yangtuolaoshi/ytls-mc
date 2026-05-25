package icu.ytlsnb.ytls.gameplay.skill;

import icu.ytlsnb.ytls.framework.render.PresentationEmitter;
import icu.ytlsnb.ytls.framework.skill.annotation.RegisterSkill;
import icu.ytlsnb.ytls.framework.skill.api.Skill;
import icu.ytlsnb.ytls.framework.skill.api.SkillContext;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

@RegisterSkill("example_slash")
public final class ExampleSlashSkill implements Skill {
    @Override
    public ResourceLocation id() {
        return ModResources.loc("example_slash");
    }

    @Override
    public int cooldownTicks() {
        return 40;
    }

    @Override
    public int castTicks() {
        return 10;
    }

    @Override
    public boolean canCast(SkillContext context) {
        return context.caster().isAlive();
    }

    @Override
    public void onCastStart(SkillContext context) {
        if (context.caster() instanceof ServerPlayer player) {
            PresentationEmitter.actionBar(player, "示例斩击 · 蓄力");
        }
    }

    @Override
    public void onCastTick(SkillContext context) {
        Vec3 pos = context.caster().position();
        PresentationEmitter.particle(context.level(), pos, "happy_villager");
    }

    @Override
    public void onCastEnd(SkillContext context) {
        if (context.caster() instanceof ServerPlayer player) {
            PresentationEmitter.sound(player, "minecraft:entity.player.attack.sweep", 1.0F, 1.2F);
            PresentationEmitter.actionBar(player, "示例斩击 · 完成");
        }
    }
}
