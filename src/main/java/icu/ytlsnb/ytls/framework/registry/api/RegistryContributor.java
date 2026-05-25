package icu.ytlsnb.ytls.framework.registry.api;

/**
 * 复杂注册项（如实体类型、创造标签）可通过实现此接口手动贡献注册逻辑。
 */
public interface RegistryContributor {
    void contribute(RegistryFacade registry);
}
