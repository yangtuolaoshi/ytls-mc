package icu.ytlsnb.ytls.framework.config.annotation;

import icu.ytlsnb.ytls.framework.config.api.ConfigScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModConfig {
    ConfigScope scope();

    String file() default "";
}
