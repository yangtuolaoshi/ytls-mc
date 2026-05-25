package icu.ytlsnb.ytls.framework.bootstrap;

/**
 * 语义化生命周期阶段，业务层通过 {@link icu.ytlsnb.ytls.framework.bootstrap.annotation.OnLifecycle} 订阅。
 */
public enum LifecyclePhase {
    CONSTRUCT,
    COMMON_SETUP,
    CLIENT_SETUP,
    SERVER_STARTING,
    SERVER_STARTED,
    SERVER_STOPPING,
    DATA_GEN
}
