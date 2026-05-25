package icu.ytlsnb.ytls.framework.ai.api;

import net.minecraft.world.entity.Mob;

/**
 * AI 行为树节点抽象。
 */
public interface BehaviorNode {
    boolean canExecute(BehaviorContext context);

    void execute(BehaviorContext context);
}
