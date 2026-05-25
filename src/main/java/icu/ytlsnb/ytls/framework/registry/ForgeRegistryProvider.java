package icu.ytlsnb.ytls.framework.registry;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.registry.api.RegistryFacade;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Forge 1.20.x 注册实现。内部使用 DeferredRegister，对外暴露 RegistryFacade 接口。
 */
public final class ForgeRegistryProvider implements RegistryFacade {
    private static final Logger LOG = FrameworkLog.of("registry");

    private final Map<RegistryKind, DeferredRegister<?>> deferredRegisters = new EnumMap<>(RegistryKind.class);
    private final Map<RegistryKind, Map<String, Supplier<?>>> entries = new EnumMap<>(RegistryKind.class);
    private final List<RegisteredEntry<?>> catalog = new ArrayList<>();

    public ForgeRegistryProvider() {
        for (RegistryKind kind : RegistryKind.values()) {
            deferredRegisters.put(kind, createDeferredRegister(kind));
            entries.put(kind, new HashMap<>());
        }
    }

    private static DeferredRegister<?> createDeferredRegister(RegistryKind kind) {
        return switch (kind) {
            case BLOCK -> DeferredRegister.create(ForgeRegistries.BLOCKS, ModConstants.MOD_ID);
            case ITEM -> DeferredRegister.create(ForgeRegistries.ITEMS, ModConstants.MOD_ID);
            case ENTITY -> DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModConstants.MOD_ID);
            case BLOCK_ENTITY -> DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModConstants.MOD_ID);
            case SOUND -> DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModConstants.MOD_ID);
        };
    }

    @Override
    public <T> void register(RegistryKind kind, String name, Supplier<T> supplier) {
        validateName(name);
        Map<String, Supplier<?>> kindEntries = entries.get(kind);
        if (kindEntries.containsKey(name)) {
            throw new IllegalStateException("Duplicate registry entry: " + kind + "/" + name);
        }

        @SuppressWarnings("unchecked")
        DeferredRegister<T> deferred = (DeferredRegister<T>) deferredRegisters.get(kind);
        RegistryObject<T> holder = deferred.register(name, supplier);
        kindEntries.put(name, holder);
        catalog.add(new RegisteredEntry<>(kind, name, holder));
        LOG.info("Registered {} -> {}", kind, id(name));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<Supplier<T>> get(RegistryKind kind, String name) {
        Supplier<?> supplier = entries.get(kind).get(name);
        return Optional.ofNullable((Supplier<T>) supplier);
    }

    @Override
    public ResourceLocation id(String name) {
        return ModResources.loc(name);
    }

    @Override
    public void bindToModBus(Object modEventBus) {
        if (!(modEventBus instanceof IEventBus bus)) {
            throw new IllegalArgumentException("modEventBus must be IEventBus");
        }
        deferredRegisters.values().forEach(deferred -> deferred.register(bus));
        LOG.info("Bound {} deferred registers to mod event bus", deferredRegisters.size());
    }

    public List<RegisteredEntry<?>> catalog() {
        return List.copyOf(catalog);
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(RegistryKind kind, String name) {
        Supplier<?> supplier = entries.get(kind).get(name);
        if (supplier == null) {
            throw new IllegalStateException("Registry entry not found: " + kind + "/" + name);
        }
        return (T) supplier.get();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Registry name must not be blank");
        }
        if (!name.matches("[a-z][a-z0-9_/]*")) {
            throw new IllegalArgumentException("Invalid registry name: " + name);
        }
    }

    public record RegisteredEntry<T>(RegistryKind kind, String name, RegistryObject<T> holder) {
        public ResourceLocation location() {
            return ModResources.loc(name);
        }
    }
}
