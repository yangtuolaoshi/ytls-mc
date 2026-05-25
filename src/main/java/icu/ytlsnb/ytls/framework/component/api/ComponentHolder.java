package icu.ytlsnb.ytls.framework.component.api;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.Optional;

/**
 * 组件容器抽象，屏蔽 Forge Capability 细节。
 */
@AutoRegisterCapability
public interface ComponentHolder {
    <T extends GameComponent> Optional<T> get(ComponentType<T> type);

    <T extends GameComponent> T getOrCreate(ComponentType<T> type);

    <T extends GameComponent> void put(ComponentType<T> type, T component);

    <T extends GameComponent> boolean has(ComponentType<T> type);

    void copyFrom(ComponentHolder other);

    void clear();
}
