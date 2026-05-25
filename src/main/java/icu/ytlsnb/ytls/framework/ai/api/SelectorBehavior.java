package icu.ytlsnb.ytls.framework.ai.api;

import java.util.List;

public final class SelectorBehavior implements BehaviorNode {
    private final List<BehaviorNode> children;

    public SelectorBehavior(List<BehaviorNode> children) {
        this.children = List.copyOf(children);
    }

    @Override
    public boolean canExecute(BehaviorContext context) {
        return children.stream().anyMatch(child -> child.canExecute(context));
    }

    @Override
    public void execute(BehaviorContext context) {
        for (BehaviorNode child : children) {
            if (child.canExecute(context)) {
                child.execute(context);
                return;
            }
        }
    }
}
