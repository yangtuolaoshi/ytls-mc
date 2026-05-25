package icu.ytlsnb.ytls.framework.ai;

import icu.ytlsnb.ytls.framework.ai.api.BehaviorNode;
import icu.ytlsnb.ytls.framework.ai.goal.BehaviorGoal;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.world.entity.Mob;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * AI 行为配置注册与应用入口。
 */
public final class AiManager {
    private static final Logger LOG = FrameworkLog.of("ai");
    private static final Map<String, AiProfile> profiles = new HashMap<>();

    private AiManager() {
    }

    public static void register(String profileId, BehaviorNode root, int priority) {
        if (profiles.containsKey(profileId)) {
            throw new IllegalStateException("Duplicate AI profile: " + profileId);
        }
        profiles.put(profileId, new AiProfile(root, priority));
        LOG.info("Registered AI profile: {} (priority={})", profileId, priority);
    }

    public static void apply(Mob mob, String profileId) {
        AiProfile profile = profiles.get(profileId);
        if (profile == null) {
            throw new IllegalStateException("AI profile not found: " + profileId);
        }
        mob.goalSelector.addGoal(profile.priority(), new BehaviorGoal(mob, profile.root()));
        LOG.debug("Applied AI profile '{}' to {}", profileId, mob.getType().getDescriptionId());
    }

    public static void scanProfiles(String basePackage, BiConsumer<String, AiProfileBuilder> registrar) {
        // 由业务层在静态块或 @OnLifecycle 中调用 registrar 注册配置
        LOG.info("AI profile scan root: {}", basePackage);
    }

    public record AiProfile(BehaviorNode root, int priority) {
    }

    public static final class AiProfileBuilder {
        private BehaviorNode root;
        private int priority = 3;

        public AiProfileBuilder root(BehaviorNode root) {
            this.root = root;
            return this;
        }

        public AiProfileBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public void register(String profileId) {
            if (root == null) {
                throw new IllegalStateException("AI profile root node is required: " + profileId);
            }
            AiManager.register(profileId, root, priority);
        }
    }
}
