package icu.ytlsnb.ytls.framework.worldrule;

import icu.ytlsnb.ytls.framework.worldrule.annotation.RegisterWorldRule;
import icu.ytlsnb.ytls.framework.worldrule.api.WorldRule;
import icu.ytlsnb.ytls.framework.util.ClasspathScanner;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class WorldRuleEngine {
    private static final Logger LOG = FrameworkLog.of("worldrule");
    private static final Map<String, WorldRule> rules = new HashMap<>();
    private static final Map<String, Boolean> enabledOverrides = new ConcurrentHashMap<>();

    private WorldRuleEngine() {
    }

    public static void scanAndRegister(String basePackage) {
        for (Class<?> clazz : ClasspathScanner.scanByAnnotation(basePackage, RegisterWorldRule.class)) {
            if (!WorldRule.class.isAssignableFrom(clazz)) {
                throw new IllegalStateException("@RegisterWorldRule must implement WorldRule: " + clazz.getName());
            }
            RegisterWorldRule meta = clazz.getAnnotation(RegisterWorldRule.class);
            register(instantiate(clazz), meta.value());
        }
        LOG.info("Registered {} world rules", rules.size());
    }

    public static void register(WorldRule rule, String id) {
        if (rules.containsKey(id)) {
            throw new IllegalStateException("Duplicate world rule id: " + id);
        }
        rules.put(id, rule);
        enabledOverrides.putIfAbsent(id, true);
        LOG.info("Registered world rule: {} (priority={})", id, rule.priority());
    }

    public static void setEnabled(String ruleId, boolean enabled) {
        if (!rules.containsKey(ruleId)) {
            throw new IllegalStateException("World rule not found: " + ruleId);
        }
        enabledOverrides.put(ruleId, enabled);
    }

    public static boolean isEnabled(String ruleId) {
        return enabledOverrides.getOrDefault(ruleId, true);
    }

    public static void tickAll(RuleContextFactory factory) {
        List<WorldRule> active = rules.values().stream()
                .filter(rule -> isEnabled(rule.id()))
                .sorted(Comparator.comparingInt(WorldRule::priority))
                .toList();
        for (WorldRule rule : active) {
            if (!rule.isEnabled(factory.level())) {
                continue;
            }
            rule.onTick(factory.contextFor(rule));
        }
    }

    public static Set<String> ruleIds() {
        return Set.copyOf(rules.keySet());
    }

    public interface RuleContextFactory {
        net.minecraft.server.level.ServerLevel level();

        icu.ytlsnb.ytls.framework.worldrule.api.RuleContext contextFor(WorldRule rule);
    }

    private static WorldRule instantiate(Class<?> clazz) {
        try {
            Constructor<?> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            return (WorldRule) ctor.newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to instantiate world rule: " + clazz.getName(), ex);
        }
    }
}
