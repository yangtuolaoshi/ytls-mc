package icu.ytlsnb.ytls.framework.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterAttribute;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterBlock;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterBlockEntity;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterCreativeTab;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterEnchantment;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterEntity;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterItem;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterMenu;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterMobEffect;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterParticle;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterRecipeSerializer;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterSound;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 将各 {@code @Register*} 注解映射为 {@link RegistryKind} 与注册模式。
 */
public final class RegistryAnnotationRules {
    private static final List<Rule> RULES = List.of(
            rule(RegisterBlock.class, RegistryKind.BLOCK, Mode.INSTANTIATE),
            rule(RegisterItem.class, RegistryKind.ITEM, Mode.INSTANTIATE),
            rule(RegisterSound.class, RegistryKind.SOUND, Mode.SUPPLIER),
            rule(RegisterEntity.class, RegistryKind.ENTITY, Mode.SUPPLIER),
            rule(RegisterBlockEntity.class, RegistryKind.BLOCK_ENTITY, Mode.SUPPLIER),
            rule(RegisterMenu.class, RegistryKind.MENU, Mode.SUPPLIER),
            rule(RegisterMobEffect.class, RegistryKind.MOB_EFFECT, Mode.SUPPLIER),
            rule(RegisterParticle.class, RegistryKind.PARTICLE, Mode.SUPPLIER),
            rule(RegisterEnchantment.class, RegistryKind.ENCHANTMENT, Mode.SUPPLIER),
            rule(RegisterAttribute.class, RegistryKind.ATTRIBUTE, Mode.SUPPLIER),
            rule(RegisterCreativeTab.class, RegistryKind.CREATIVE_TAB, Mode.SUPPLIER),
            rule(RegisterRecipeSerializer.class, RegistryKind.RECIPE_SERIALIZER, Mode.SUPPLIER)
    );

    private RegistryAnnotationRules() {
    }

    public static List<Rule> allRules() {
        return RULES;
    }

    /**
     * 解析类上的专用注册注解（不含 {@link icu.ytlsnb.ytls.framework.registry.annotation.RegisterEntry}）。
     */
    public static Optional<ResolvedRule> resolveDedicated(Class<?> clazz) {
        List<ResolvedRule> matches = new ArrayList<>();
        for (Rule rule : RULES) {
            if (clazz.isAnnotationPresent(rule.annotationType())) {
                Annotation annotation = clazz.getAnnotation(rule.annotationType());
                matches.add(new ResolvedRule(rule.kind(), rule.mode(), readValue(annotation)));
            }
        }
        if (matches.isEmpty()) {
            return Optional.empty();
        }
        if (matches.size() > 1) {
            throw new IllegalStateException(
                    "Multiple registry annotations on one class (use only one): " + clazz.getName());
        }
        return Optional.of(matches.get(0));
    }

    public static Mode modeForKind(RegistryKind kind) {
        return switch (kind) {
            case BLOCK, ITEM -> Mode.INSTANTIATE;
            default -> Mode.SUPPLIER;
        };
    }

    private static Rule rule(Class<? extends Annotation> annotationType, RegistryKind kind, Mode mode) {
        return new Rule(annotationType, kind, mode);
    }

    private static String readValue(Annotation annotation) {
        try {
            Object value = annotation.annotationType().getMethod("value").invoke(annotation);
            return String.valueOf(value);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(
                    "Registry annotation must declare String value(): " + annotation.annotationType().getName(), ex);
        }
    }

    public enum Mode {
        /** 无参构造实例化，类型需继承/实现对应注册条目类型 */
        INSTANTIATE,
        /** 实现 {@link icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier} */
        SUPPLIER
    }

    public record Rule(Class<? extends Annotation> annotationType, RegistryKind kind, Mode mode) {
    }

    public record ResolvedRule(RegistryKind kind, Mode mode, String name) {
    }
}
