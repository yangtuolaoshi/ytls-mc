package icu.ytlsnb.ytls.framework.registry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注册方块实体类型。目标类须实现 {@link icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier}。
 * 工厂内可通过 {@link icu.ytlsnb.ytls.framework.registry.RegistryAccess#block(String)} 引用已注册方块。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterBlockEntity {
    String value();
}
