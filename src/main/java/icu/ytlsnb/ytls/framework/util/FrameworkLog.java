package icu.ytlsnb.ytls.framework.util;

import icu.ytlsnb.ytls.ModConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一日志入口，所有框架模块通过模块标识输出日志。
 */
public final class FrameworkLog {
    private static final String PREFIX = "[" + ModConstants.MOD_ID + "|framework";

    private FrameworkLog() {
    }

    public static Logger of(String module) {
        return LoggerFactory.getLogger(PREFIX + "|" + module + "]");
    }
}
