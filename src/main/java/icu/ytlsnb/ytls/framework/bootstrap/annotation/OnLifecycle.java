package icu.ytlsnb.ytls.framework.bootstrap.annotation;

import icu.ytlsnb.ytls.framework.bootstrap.LifecyclePhase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnLifecycle {
    LifecyclePhase value();
}
