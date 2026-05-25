package icu.ytlsnb.ytls.framework.util;

import icu.ytlsnb.ytls.ModConstants;
import net.minecraft.resources.ResourceLocation;

/**
 * 模组资源路径工具，避免硬编码 mod id。
 */
public final class ModResources {
    private ModResources() {
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(ModConstants.MOD_ID, path);
    }

    public static String translationKey(String category, String name) {
        return category + "." + ModConstants.MOD_ID + "." + name;
    }
}
