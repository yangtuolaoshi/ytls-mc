package icu.ytlsnb.ytls.gameplay.common;

import icu.ytlsnb.ytls.framework.ai.AiManager;
import icu.ytlsnb.ytls.framework.ai.api.LeafBehavior;
import icu.ytlsnb.ytls.framework.ai.api.SelectorBehavior;
import icu.ytlsnb.ytls.framework.bootstrap.LifecyclePhase;
import icu.ytlsnb.ytls.framework.bootstrap.annotation.OnLifecycle;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import org.slf4j.Logger;

import java.util.List;

/**
 * 第二优先级示例：注册 AI 行为配置。
 */
public final class ExampleSecondPrioritySetup {
    private static final Logger LOG = FrameworkLog.of("gameplay");

    private ExampleSecondPrioritySetup() {
    }

    @OnLifecycle(LifecyclePhase.COMMON_SETUP)
    public static void registerAiProfiles() {
        AiManager.register("example_aggressive", new SelectorBehavior(List.of(
                new LeafBehavior(
                        ctx -> ctx.target() != null && ctx.mob().distanceToSqr(ctx.target()) > 4.0D,
                        ctx -> ctx.mob().getNavigation().moveTo(ctx.target(), 1.1D)
                ),
                new LeafBehavior(
                        ctx -> ctx.target() != null,
                        ctx -> ctx.mob().doHurtTarget(ctx.target())
                )
        )), 2);
        LOG.info("Registered example AI profile: example_aggressive");
    }
}
