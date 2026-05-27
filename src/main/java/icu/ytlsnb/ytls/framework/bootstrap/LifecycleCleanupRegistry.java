package icu.ytlsnb.ytls.framework.bootstrap;

import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 统一关闭/清理注册表。在 {@link LifecyclePhase#SHUTDOWN} 阶段按注册逆序执行。
 * <p>
 * 业务层或框架子模块可在初始化时注册 {@link Runnable}，避免在多处散落清理逻辑。
 */
public final class LifecycleCleanupRegistry {
    private static final Logger LOG = FrameworkLog.of("lifecycle");
    private static final List<Runnable> CLEANUPS = new ArrayList<>();

    private LifecycleCleanupRegistry() {
    }

    /**
     * 注册关闭时执行的清理任务（后注册的先执行）。
     */
    public static void register(String name, Runnable cleanup) {
        if (cleanup == null) {
            throw new IllegalArgumentException("cleanup must not be null");
        }
        CLEANUPS.add(() -> {
            try {
                LOG.debug("Running lifecycle cleanup: {}", name);
                cleanup.run();
            } catch (Exception ex) {
                throw new IllegalStateException("Lifecycle cleanup failed: " + name, ex);
            }
        });
        LOG.debug("Registered lifecycle cleanup: {}", name);
    }

    public static void register(Runnable cleanup) {
        register("anonymous@" + Integer.toHexString(cleanup.hashCode()), cleanup);
    }

    static void runAll() {
        if (CLEANUPS.isEmpty()) {
            return;
        }
        LOG.info("Running {} lifecycle cleanup task(s)", CLEANUPS.size());
        List<Runnable> reversed = new ArrayList<>(CLEANUPS);
        Collections.reverse(reversed);
        for (Runnable cleanup : reversed) {
            cleanup.run();
        }
        CLEANUPS.clear();
    }

    static void resetForTesting() {
        CLEANUPS.clear();
    }
}
