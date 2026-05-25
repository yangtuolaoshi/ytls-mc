package icu.ytlsnb.ytls.framework.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 配置构建辅助，简化 ForgeConfigSpec 声明。
 */
public final class ConfigBuilder {
    private final ForgeConfigSpec.Builder builder;

    private ConfigBuilder(ForgeConfigSpec.Builder builder) {
        this.builder = builder;
    }

    public static ConfigBuilder create(String title) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment(title);
        return new ConfigBuilder(builder);
    }

    public ConfigBuilder comment(String comment) {
        builder.comment(comment);
        return this;
    }

    public ConfigBuilder push(String path) {
        builder.push(path);
        return this;
    }

    public ConfigBuilder pop() {
        builder.pop();
        return this;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> define(String path, boolean defaultValue) {
        return builder.define(path, defaultValue);
    }

    public ForgeConfigSpec.ConfigValue<Integer> define(String path, int defaultValue, int min, int max) {
        return builder.defineInRange(path, defaultValue, min, max);
    }

    public ForgeConfigSpec.ConfigValue<Double> define(String path, double defaultValue, double min, double max) {
        return builder.defineInRange(path, defaultValue, min, max);
    }

    public ForgeConfigSpec.ConfigValue<String> define(String path, String defaultValue) {
        return builder.define(path, defaultValue);
    }

    public ForgeConfigSpec build() {
        return builder.build();
    }
}
