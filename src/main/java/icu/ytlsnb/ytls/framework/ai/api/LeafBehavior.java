package icu.ytlsnb.ytls.framework.ai.api;

import java.util.function.Predicate;

public final class LeafBehavior implements BehaviorNode {
    private final Predicate<BehaviorContext> condition;
    private final BehaviorAction action;

    @FunctionalInterface
    public interface BehaviorAction {
        void run(BehaviorContext context);
    }

    public LeafBehavior(Predicate<BehaviorContext> condition, BehaviorAction action) {
        this.condition = condition;
        this.action = action;
    }

    @Override
    public boolean canExecute(BehaviorContext context) {
        return condition.test(context);
    }

    @Override
    public void execute(BehaviorContext context) {
        action.run(context);
    }
}
