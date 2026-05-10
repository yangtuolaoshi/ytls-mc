package icu.ytlsnb.ytls.init;

import net.minecraftforge.eventbus.api.IEventBus;

/**
 * 将所有 {@link net.minecraftforge.registries.DeferredRegister} 挂到模组加载总线。
 * 新增注册表时只改此处与对应 {@code Mod*} 类即可。
 */
public final class ModBusSetup {
    private ModBusSetup() {
    }

    public static void subscribeDeferredRegisters(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }
}
