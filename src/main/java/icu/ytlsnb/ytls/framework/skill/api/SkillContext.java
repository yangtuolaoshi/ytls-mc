package icu.ytlsnb.ytls.framework.skill.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class SkillContext {
    private final LivingEntity caster;
    private final ServerLevel level;
    private LivingEntity target;
    private Vec3 aimPos;

    public SkillContext(LivingEntity caster) {
        this.caster = caster;
        this.level = (ServerLevel) caster.level();
    }

    public LivingEntity caster() {
        return caster;
    }

    public ServerLevel level() {
        return level;
    }

    public LivingEntity target() {
        return target;
    }

    public SkillContext withTarget(LivingEntity target) {
        this.target = target;
        return this;
    }

    public Vec3 aimPos() {
        return aimPos;
    }

    public SkillContext withAimPos(Vec3 aimPos) {
        this.aimPos = aimPos;
        return this;
    }
}
