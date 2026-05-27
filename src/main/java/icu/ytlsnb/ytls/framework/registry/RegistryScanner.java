package icu.ytlsnb.ytls.framework.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterEntry;
import icu.ytlsnb.ytls.framework.registry.api.RegistryContributor;
import icu.ytlsnb.ytls.framework.registry.api.RegistryFacade;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import icu.ytlsnb.ytls.framework.util.ClasspathScanner;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 扫描 gameplay 包中带注册注解的类，并自动完成注册。
 * <p>
 * 支持 12 类 {@link RegistryKind} 的专用注解；扫描按 {@link RegistryKind#scanOrder()} 分阶段执行。
 */
public final class RegistryScanner {
    private static final Logger LOG = FrameworkLog.registry();

    private RegistryScanner() {
    }

    public static void scanAndRegister(String basePackage, RegistryFacade registry) {
        List<Class<?>> candidates = new ArrayList<>(ClasspathScanner.scanPackage(basePackage));
        candidates.sort(Comparator.comparingInt(RegistryScanner::scanOrderForClass));

        int registered = 0;
        for (Class<?> clazz : candidates) {
            if (tryRegisterClass(registry, clazz)) {
                registered++;
            }
        }
        scanContributors(basePackage, registry);
        logScanSummary(registry, registered);
    }

    private static int scanOrderForClass(Class<?> clazz) {
        return RegistryAnnotationRules.resolveDedicated(clazz)
                .map(rule -> rule.kind().scanOrder())
                .orElseGet(() -> {
                    RegisterEntry entry = clazz.getAnnotation(RegisterEntry.class);
                    return entry != null ? entry.kind().scanOrder() : Integer.MAX_VALUE;
                });
    }

    private static boolean tryRegisterClass(RegistryFacade registry, Class<?> clazz) {
        var dedicated = RegistryAnnotationRules.resolveDedicated(clazz);
        if (dedicated.isPresent()) {
            RegistryAnnotationRules.ResolvedRule rule = dedicated.get();
            applyRule(registry, rule.kind(), rule.mode(), rule.name(), clazz);
            return true;
        }
        RegisterEntry entry = clazz.getAnnotation(RegisterEntry.class);
        if (entry != null) {
            RegistryAnnotationRules.Mode mode = RegistryAnnotationRules.modeForKind(entry.kind());
            applyRule(registry, entry.kind(), mode, entry.value(), clazz);
            return true;
        }
        return false;
    }

    private static void applyRule(
            RegistryFacade registry,
            RegistryKind kind,
            RegistryAnnotationRules.Mode mode,
            String name,
            Class<?> clazz) {
        if (mode == RegistryAnnotationRules.Mode.INSTANTIATE) {
            registerInstantiable(registry, kind, name, clazz);
        } else {
            registerSupplier(registry, kind, name, clazz);
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

    private static void logScanSummary(RegistryFacade registry, int annotatedCount) {
        if (!(registry instanceof ForgeRegistryProvider provider)) {
            LOG.info("Registry scan complete: {} annotated class(es)", annotatedCount);
            return;
        }
        Map<RegistryKind, Integer> counts = new EnumMap<>(RegistryKind.class);
        for (var entry : provider.catalog()) {
            counts.merge(entry.kind(), 1, Integer::sum);
        }
        StringBuilder summary = new StringBuilder("Registry scan complete: ")
                .append(annotatedCount)
                .append(" annotated class(es), catalog=");
        for (RegistryKind kind : RegistryKind.values()) {
            int count = counts.getOrDefault(kind, 0);
            if (count > 0) {
                summary.append(' ').append(kind.name()).append('=').append(count);
            }
        }
        LOG.info(summary.toString());
        for (RegistryKind kind : RegistryKind.values()) {
            if (!counts.containsKey(kind)) {
                LOG.debug("No entries registered for kind {}", kind);
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
                    clazz.getName() + " is not compatible with registry kind " + kind
                            + " (expected " + kind.entryClass().getSimpleName() + ")");
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
                    clazz.getName() + " must implement RegistrySupplier for registry kind " + kind);
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
