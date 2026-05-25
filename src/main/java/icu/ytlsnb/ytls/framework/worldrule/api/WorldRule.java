package icu.ytlsnb.ytls.framework.worldrule.api;

import net.minecraft.server.level.ServerLevel;

/**
 * 世界级规则抽象，可按维度/区域启停。
 */
public interface WorldRule {
    String id();

    int priority();

    RuleScope scope();

    default boolean isEnabled(ServerLevel level) {
        return true;
    }

    default void onEnable(ServerLevel level) {
    }

    default void onDisable(ServerLevel level) {
    }

    void onTick(RuleContext context);
}
