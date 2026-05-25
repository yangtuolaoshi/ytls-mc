package icu.ytlsnb.ytls.framework.debug;

import icu.ytlsnb.ytls.framework.config.ConfigBuilder;
import icu.ytlsnb.ytls.framework.config.annotation.ModConfig;
import icu.ytlsnb.ytls.framework.config.api.ConfigScope;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 框架调试配置，可通过配置文件或命令开关调试能力。
 */
@ModConfig(scope = ConfigScope.COMMON, file = "ytls-framework.toml")
public final class FrameworkDebugConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DEBUG_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LOG_REGISTRY;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LOG_EVENTS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LOG_NETWORK;

    static {
        ConfigBuilder builder = ConfigBuilder.create("YTLS Framework Debug Settings");
        builder.push("debug");
        DEBUG_ENABLED = builder.define("enabled", false);
        LOG_REGISTRY = builder.define("logRegistry", true);
        LOG_EVENTS = builder.define("logEvents", false);
        LOG_NETWORK = builder.define("logNetwork", false);
        builder.pop();
        SPEC = builder.build();
    }

    private FrameworkDebugConfig() {
    }

    public static boolean isDebugEnabled() {
        return DEBUG_ENABLED.get();
    }
}
