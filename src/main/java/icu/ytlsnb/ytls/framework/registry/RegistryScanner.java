package icu.ytlsnb.ytls.framework.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterBlock;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterEntity;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterEntry;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterItem;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterSound;
import icu.ytlsnb.ytls.framework.registry.api.RegistryContributor;
import icu.ytlsnb.ytls.framework.registry.api.RegistryFacade;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import icu.ytlsnb.ytls.framework.util.ClasspathScanner;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

/**
 * 扫描 gameplay 包中带注册注解的类，并自动完成注册。
 */
public final class RegistryScanner {
    private static final Logger LOG = FrameworkLog.registry();

    private RegistryScanner() {
    }

    public static void scanAndRegister(String basePackage, RegistryFacade registry) {
        scanAnnotatedClasses(basePackage, registry);
        scanContributors(basePackage, registry);
    }

    private static void scanAnnotatedClasses(String basePackage, RegistryFacade registry) {
        for (Class<?> clazz : ClasspathScanner.scanPackage(basePackage)) {
            RegisterBlock block = clazz.getAnnotation(RegisterBlock.class);
            if (block != null) {
                registerInstantiable(registry, RegistryKind.BLOCK, block.value(), clazz);
                continue;
            }
            RegisterItem item = clazz.getAnnotation(RegisterItem.class);
            if (item != null) {
                registerInstantiable(registry, RegistryKind.ITEM, item.value(), clazz);
                continue;
            }
            RegisterSound sound = clazz.getAnnotation(RegisterSound.class);
            if (sound != null) {
                registerSupplier(registry, RegistryKind.SOUND, sound.value(), clazz);
                continue;
            }
            RegisterEntity entity = clazz.getAnnotation(RegisterEntity.class);
            if (entity != null) {
                registerSupplier(registry, RegistryKind.ENTITY, entity.value(), clazz);
                continue;
            }
            RegisterEntry entry = clazz.getAnnotation(RegisterEntry.class);
            if (entry != null) {
                if (RegistryKind.BLOCK == entry.kind() || RegistryKind.ITEM == entry.kind()) {
                    registerInstantiable(registry, entry.kind(), entry.value(), clazz);
                } else {
                    registerSupplier(registry, entry.kind(), entry.value(), clazz);
                }
            }
        }
    }

    private static void scanContributors(String basePackage, RegistryFacade registry) {
        for (Class<?> clazz : ClasspathScanner.scanSubtypes(basePackage, RegistryContributor.class)) {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }
            try {
                RegistryContributor contributor = (RegistryContributor) clazz.getDeclaredConstructor().newInstance();
                contributor.contribute(registry);
                LOG.info("Applied registry contributor: {}", clazz.getName());
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to instantiate RegistryContributor: " + clazz.getName(), ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void registerInstantiable(RegistryFacade registry, RegistryKind kind, String name, Class<?> clazz) {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Registry name missing on " + clazz.getName());
        }
        if (!kind.entryClass().isAssignableFrom(clazz)) {
            throw new IllegalStateException(
                    clazz.getName() + " is not compatible with registry kind " + kind);
        }
        Supplier<T> supplier = () -> {
            try {
                Constructor<?> ctor = clazz.getDeclaredConstructor();
                ctor.setAccessible(true);
                return (T) ctor.newInstance();
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to instantiate registered class: " + clazz.getName(), ex);
            }
        };
        registry.register(kind, name, supplier);
        LOG.info("Auto-registered {} as {} ({})", clazz.getSimpleName(), kind, name);
    }

    @SuppressWarnings("unchecked")
    private static <T> void registerSupplier(RegistryFacade registry, RegistryKind kind, String name, Class<?> clazz) {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Registry name missing on " + clazz.getName());
        }
        if (!RegistrySupplier.class.isAssignableFrom(clazz)) {
            throw new IllegalStateException(
                    clazz.getName() + " must implement RegistrySupplier for kind " + kind);
        }
        Supplier<T> supplier = () -> {
            try {
                Constructor<?> ctor = clazz.getDeclaredConstructor();
                ctor.setAccessible(true);
                @SuppressWarnings("rawtypes")
                RegistrySupplier raw = (RegistrySupplier) ctor.newInstance();
                return (T) raw.get();
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to create registry supplier: " + clazz.getName(), ex);
            }
        };
        registry.register(kind, name, supplier);
        LOG.info("Auto-registered supplier {} as {} ({})", clazz.getSimpleName(), kind, name);
    }
}
