package icu.ytlsnb.ytls.framework.util;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.debug.FrameworkDebugConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一日志入口，所有框架模块通过模块标识输出日志。
 * 可通过 {@code ytls-framework.toml} 中 debug.logRegistry / logEvents / logNetwork 开关细粒度控制。
 */
public final class FrameworkLog {
    private static final String PREFIX = "[" + ModConstants.MOD_ID + "|framework";

    private FrameworkLog() {
    }

    public static Logger of(String module) {
        return LoggerFactory.getLogger(PREFIX + "|" + module + "]");
    }

    public static Logger registry() {
        return of("registry");
    }

    public static Logger events() {
        return of("event");
    }

    public static Logger network() {
        return of("network");
    }

    public static void registryInfo(String message, Object... args) {
        if (isRegistryLogEnabled()) {
            registry().info(message, args);
        }
    }

    public static void eventDebug(String message, Object... args) {
        if (isEventLogEnabled()) {
            events().debug(message, args);
        }
    }

    public static void networkDebug(String message, Object... args) {
        if (isNetworkLogEnabled()) {
            network().debug(message, args);
        }
    }

    private static boolean isRegistryLogEnabled() {
        try {
            return FrameworkDebugConfig.LOG_REGISTRY.get();
        } catch (Exception ex) {
            return true;
        }
    }

    private static boolean isEventLogEnabled() {
        try {
            return FrameworkDebugConfig.LOG_EVENTS.get();
        } catch (Exception ex) {
            return false;
        }
    }

    private static boolean isNetworkLogEnabled() {
        try {
            return FrameworkDebugConfig.LOG_NETWORK.get();
        } catch (Exception ex) {
            return false;
        }
    }
}
