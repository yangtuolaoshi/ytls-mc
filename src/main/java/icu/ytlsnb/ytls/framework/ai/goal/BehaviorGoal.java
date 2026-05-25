package icu.ytlsnb.ytls.framework.ai.goal;

import icu.ytlsnb.ytls.framework.ai.api.BehaviorContext;
import icu.ytlsnb.ytls.framework.ai.api.BehaviorNode;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 将 {@link BehaviorNode} 包装为原版 Goal，供 Mob 的 goalSelector 使用。
 */
public final class BehaviorGoal extends Goal {
    private final Mob mob;
    private final BehaviorNode root;
    private final BehaviorContext context;

    public BehaviorGoal(Mob mob, BehaviorNode root, EnumSet<Goal.Flag> flags) {
        this.mob = mob;
        this.root = root;
        this.context = new BehaviorContext(mob);
        this.setFlags(flags);
    }

    public BehaviorGoal(Mob mob, BehaviorNode root) {
        this(mob, root, EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return root.canExecute(context);
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        root.execute(context);
    }
}
