package icu.ytlsnb.ytls.framework.util;

import cn.hutool.core.lang.ClassScanner;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 Hutool 的包路径扫描器，扫描失败时抛出明确异常而非静默忽略。
 */
public final class ClasspathScanner {
    private static final Logger LOG = FrameworkLog.of("scan");

    private ClasspathScanner() {
    }

    public static Set<Class<?>> scanPackage(String basePackage) {
        try {
            Set<Class<?>> classes = ClassScanner.scanPackage(basePackage);
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
            Set<Class<?>> classes = ClassScanner.scanPackageByAnnotation(basePackage, annotation);
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
