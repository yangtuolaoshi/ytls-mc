package icu.ytlsnb.ytls.framework.registry.api;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 注册体系抽象接口。版本迁移时只需替换 Forge 实现，业务层代码不变。
 */
public interface RegistryFacade {
    <T> void register(RegistryKind kind, String name, Supplier<T> supplier);

    <T> Optional<Supplier<T>> get(RegistryKind kind, String name);

    ResourceLocation id(String name);

    void bindToModBus(Object modEventBus);
}
