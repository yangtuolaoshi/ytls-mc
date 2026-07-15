package icu.ytlsnb.ytls.framework.util;

import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.lang.Filter;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 Hutool 的包路径扫描器，扫描失败时抛出明确异常而非静默忽略。
 * <p>
 * 专用服上不得加载客户端类（会触发 RuntimeDistCleaner），因此按包名约定
 * （含 {@code .client.} 段）在 {@link Class#forName} 之前跳过。
 */
public final class ClasspathScanner {
    private static final Logger LOG = FrameworkLog.of("scan");

    private ClasspathScanner() {
    }

    /** 是否为客户端专用类（包路径含 {@code .client.}）。 */
    static boolean isClientClassName(String className) {
        if (className == null || className.isEmpty()) {
            return false;
        }
        String normalized = className.replace('/', '.').replace('\\', '.');
        return normalized.contains(".client.");
    }

    private static ClassScanner createScanner(String basePackage, Filter<Class<?>> classFilter) {
        return new ClassScanner(basePackage, classFilter) {
            @Override
            protected Class<?> loadClass(String className) {
                if (isClientClassName(className)) {
                    LOG.debug("Skipping client class during scan: {}", className);
                    return null;
                }
                return super.loadClass(className);
            }
        };
    }

    public static Set<Class<?>> scanPackage(String basePackage) {
        try {
            Set<Class<?>> classes = createScanner(basePackage, null).scan();
            if (classes == null) {
                return Collections.emptySet();
            }
            LOG.debug("Scanned package '{}' -> {} classes", basePackage, classes.size());
            return classes;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to scan package: " + basePackage, ex);
        }
    }

    public static <A extends Annotation> Set<Class<?>> scanByAnnotation(String basePackage, Class<A> annotation) {
        try {
            Set<Class<?>> classes = createScanner(
                    basePackage,
                    clazz -> clazz.isAnnotationPresent(annotation)
            ).scan();
            if (classes == null) {
                return Collections.emptySet();
            }
            LOG.debug("Scanned package '{}' for @{} -> {} classes", basePackage, annotation.getSimpleName(), classes.size());
            return classes;
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Failed to scan package '" + basePackage + "' for @" + annotation.getSimpleName(), ex);
        }
    }

    public static Set<Class<?>> scanSubtypes(String basePackage, Class<?> superType) {
        return scanPackage(basePackage).stream()
                .filter(clazz -> superType.isAssignableFrom(clazz) && clazz != superType)
                .collect(Collectors.toSet());
    }
}
