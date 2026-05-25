package icu.ytlsnb.ytls.gameplay.worldrule;

import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import icu.ytlsnb.ytls.framework.worldrule.annotation.RegisterWorldRule;
import icu.ytlsnb.ytls.framework.worldrule.api.RuleContext;
import icu.ytlsnb.ytls.framework.worldrule.api.RuleScope;
import icu.ytlsnb.ytls.framework.worldrule.api.WorldRule;
import icu.ytlsnb.ytls.framework.worldrule.WorldRuleEngine;
import org.slf4j.Logger;

/**
 * 示例世界规则：每 1200 tick 输出一次日志（可通过 /ytls rule 开关）。
 */
@RegisterWorldRule(value = "example_blood_moon", priority = 200)
public final class ExampleBloodMoonRule implements WorldRule {
    private static final Logger LOG = FrameworkLog.of("gameplay-rule");

    @Override
    public String id() {
        return "example_blood_moon";
    }

    @Override
    public int priority() {
        return 200;
    }

    @Override
    public RuleScope scope() {
        return RuleScope.GLOBAL;
    }

    @Override
    public void onTick(RuleContext context) {
        if (!WorldRuleEngine.isEnabled(id())) {
            return;
        }
        if (context.gameTime() % 1200L != 0L) {
            return;
        }
        LOG.info("[BloodMoon Demo] world tick at {} in {}", context.gameTime(), context.dimension().location());
    }
}
