package icu.ytlsnb.ytls.framework.ai.api;

import java.util.List;

public final class SequenceBehavior implements BehaviorNode {
    private final List<BehaviorNode> children;

    public SequenceBehavior(List<BehaviorNode> children) {
        this.children = List.copyOf(children);
    }

    @Override
    public boolean canExecute(BehaviorContext context) {
        return children.stream().allMatch(child -> child.canExecute(context));
    }

    @Override
    public void execute(BehaviorContext context) {
        for (BehaviorNode child : children) {
            if (child.canExecute(context)) {
                child.execute(context);
            }
        }
    }
}
