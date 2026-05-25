package icu.ytlsnb.ytls.framework.registry.annotation;

import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterEntry {
    RegistryKind kind();

    String value();
}
