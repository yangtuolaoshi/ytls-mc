package icu.ytlsnb.ytls.core;

import net.minecraft.resources.ResourceLocation;

/**
 * 统一构造本模组 {@link ResourceLocation}，避免散落魔法字符串或错误的命名空间（例如 {@code "modid"}）。
 */
public final class ModResources {
    private ModResources() {
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(ModConstants.MOD_ID, path);
    }
}
