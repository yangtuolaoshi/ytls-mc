package icu.ytlsnb.ytls.framework.registry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注册音效。目标类须实现 {@link icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier}{@code <SoundEvent>}。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterSound {
    String value();
}
