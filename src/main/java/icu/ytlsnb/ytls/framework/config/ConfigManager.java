package icu.ytlsnb.ytls.framework.config;

import icu.ytlsnb.ytls.framework.config.annotation.ModConfig;
import icu.ytlsnb.ytls.framework.config.api.ConfigScope;
import icu.ytlsnb.ytls.framework.util.ClasspathScanner;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 统一配置管理：扫描带 {@link ModConfig} 的配置类并注册 Forge 配置规范。
 */
public final class ConfigManager {
    private static final Logger LOG = FrameworkLog.of("config");

    private final List<ConfigBinding> bindings = new ArrayList<>();

    public void scanAndRegister(String basePackage) {
        for (Class<?> clazz : ClasspathScanner.scanByAnnotation(basePackage, ModConfig.class)) {
            ModConfig meta = clazz.getAnnotation(ModConfig.class);
            registerConfigClass(meta, clazz);
        }
    }

    private void registerConfigClass(ModConfig meta, Class<?> clazz) {
        try {
            Field specField = clazz.getField("SPEC");
            if (!(specField.get(null) instanceof ForgeConfigSpec spec)) {
                throw new IllegalStateException("Config class missing public static ForgeConfigSpec SPEC: " + clazz.getName());
            }
            Type forgeType = toForgeType(meta.scope());
            String fileName = meta.file().isBlank() ? clazz.getSimpleName().toLowerCase() + ".toml" : meta.file();
            ModLoadingContext.get().registerConfig(forgeType, spec, fileName);
            bindings.add(new ConfigBinding(clazz, meta.scope(), fileName));
            LOG.info("Registered config {} ({})", clazz.getSimpleName(), fileName);
        } catch (NoSuchFieldException ex) {
            throw new IllegalStateException("Config class must expose public static ForgeConfigSpec SPEC: " + clazz.getName(), ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Failed to read config spec: " + clazz.getName(), ex);
        }
    }

    public List<ConfigBinding> bindings() {
        return List.copyOf(bindings);
    }

    private static Type toForgeType(ConfigScope scope) {
        return switch (scope) {
            case CLIENT -> Type.CLIENT;
            case COMMON -> Type.COMMON;
            case SERVER -> Type.SERVER;
        };
    }

    public record ConfigBinding(Class<?> configClass, ConfigScope scope, String fileName) {
    }
}
