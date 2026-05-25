package icu.ytlsnb.ytls.gameplay.config;

import icu.ytlsnb.ytls.framework.config.ConfigBuilder;
import icu.ytlsnb.ytls.framework.config.annotation.ModConfig;
import icu.ytlsnb.ytls.framework.config.api.ConfigScope;
import net.minecraftforge.common.ForgeConfigSpec;

@ModConfig(scope = ConfigScope.COMMON, file = "ytls-gameplay.toml")
public final class GameplayConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> EXAMPLE_VALUE;

    static {
        ConfigBuilder builder = ConfigBuilder.create("YTLS Gameplay Settings");
        builder.push("general");
        EXAMPLE_VALUE = builder.define("exampleValue", 10, 1, 100);
        builder.pop();
        SPEC = builder.build();
    }

    private GameplayConfig() {
    }
}
