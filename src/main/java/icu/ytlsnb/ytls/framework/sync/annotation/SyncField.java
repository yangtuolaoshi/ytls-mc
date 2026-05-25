package icu.ytlsnb.ytls.framework.sync.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要服务端权威同步的字段，配合 {@link icu.ytlsnb.ytls.framework.sync.SyncHelper} 使用。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SyncField {
    /** 服务端写入后是否立即推送给跟踪该实体的客户端 */
    boolean immediate() default true;
}
