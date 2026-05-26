package icu.ytlsnb.ytls.framework.bootstrap;

/**
 * 语义化生命周期阶段，业务层通过 {@link icu.ytlsnb.ytls.framework.bootstrap.annotation.OnLifecycle} 订阅。
 */
public enum LifecyclePhase {
    CONSTRUCT,
    COMMON_SETUP,
    CLIENT_SETUP,
    DATA_GEN,
    SERVER_STARTING,
    SERVER_STARTED,
    SERVER_STOPPING,
    WORLD_LOAD,
    WORLD_UNLOAD,
    RESOURCE_RELOAD,
    CONFIG_RELOAD
}
