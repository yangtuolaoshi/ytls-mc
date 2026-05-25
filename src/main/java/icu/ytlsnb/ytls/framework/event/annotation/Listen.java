package icu.ytlsnb.ytls.framework.event.annotation;

import icu.ytlsnb.ytls.framework.event.api.GameEventPriority;
import icu.ytlsnb.ytls.framework.event.api.GameEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listen {
    GameEventType value();

    GameEventPriority priority() default GameEventPriority.NORMAL;
}
