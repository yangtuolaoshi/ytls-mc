package icu.ytlsnb.ytls.framework.registry.api;

/**
 * 供 {@link icu.ytlsnb.ytls.framework.registry.annotation.RegisterSound} 等注解使用的注册工厂。
 * 复杂注册对象（音效、实体类型等）无法仅靠无参构造实例化时使用此接口。
 */
@FunctionalInterface
public interface RegistrySupplier<T> {
    T get();
}
