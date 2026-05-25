package icu.ytlsnb.ytls.framework.component.annotation;

import icu.ytlsnb.ytls.framework.component.api.ComponentTarget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterComponent {
    String value();

    ComponentTarget[] targets() default {};
}
