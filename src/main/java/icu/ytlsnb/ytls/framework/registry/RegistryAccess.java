package icu.ytlsnb.ytls.framework.registry;

import icu.ytlsnb.ytls.framework.registry.api.RegistryFacade;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 业务层访问已注册对象的便捷入口，避免直接操作 RegistryObject。
 */
public final class RegistryAccess {
    private static ForgeRegistryProvider provider;

    private RegistryAccess() {
    }

    public static void bind(ForgeRegistryProvider forgeProvider) {
        provider = forgeProvider;
    }

    public static Block block(String name) {
        return provider.resolve(RegistryKind.BLOCK, name);
    }

    public static Item item(String name) {
        return provider.resolve(RegistryKind.ITEM, name);
    }

    public static RegistryFacade facade() {
        return provider;
    }

    public static ForgeRegistryProvider provider() {
        return provider;
    }
}
