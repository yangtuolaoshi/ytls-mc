package icu.ytlsnb.ytls.framework.ai.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public final class BehaviorContext {
    private final Mob mob;
    private LivingEntity target;

    public BehaviorContext(Mob mob) {
        this.mob = mob;
        this.target = mob.getTarget();
    }

    public Mob mob() {
        return mob;
    }

    public LivingEntity target() {
        return target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
        mob.setTarget(target);
    }
}
