package icu.ytlsnb.ytls.framework.registry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注册配方序列化器。目标类须实现 {@link icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier}。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterRecipeSerializer {
    String value();
}
