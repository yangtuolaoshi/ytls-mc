package icu.ytlsnb.ytls.framework.debug;

import net.minecraftforge.fml.loading.FMLLoader;

/**
 * 开发环境检测。
 */
public final class DevEnvironment {
    private DevEnvironment() {
    }

    public static boolean isDevelopment() {
        return !FMLLoader.isProduction();
    }

    public static boolean isDebugActive() {
        return isDevelopment() || FrameworkDebugConfig.isDebugEnabled();
    }
}
