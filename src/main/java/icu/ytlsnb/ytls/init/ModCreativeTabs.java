package icu.ytlsnb.ytls.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static icu.ytlsnb.ytls.core.ModConstants.MOD_ID;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> TEST_TAB = CREATIVE_MODE_TABS.register(
            "test_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("测试物品栏"))
                    .icon(() -> new ItemStack(ModItems.OBSIDIAN_INGOT.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.OBSIDIAN_INGOT.get());
                        output.accept(ModItems.OBSIDIAN_APPLE.get());
                        output.accept(ModItems.OBSIDIAN_SWORD.get());
                        output.accept(ModItems.OBSIDIAN_PICKAXE.get());
                        output.accept(ModItems.OBSIDIAN_HELMET.get());
                        output.accept(ModItems.OBSIDIAN_CHESTPLATE.get());
                        output.accept(ModItems.OBSIDIAN_LEGGINGS.get());
                        output.accept(ModItems.OBSIDIAN_BOOTS.get());
                        output.accept(ModItems.OBSIDIAN_BLOCK.get());
                        output.accept(ModItems.OBSIDIAN_COUNTER.get());
                        output.accept(ModItems.BAGUA_FURNACE.get());
                        output.accept(ModItems.BAGUA_SLAG.get());
                        output.accept(ModItems.RUYI_JINGU_BANG.get());
                        output.accept(ModItems.JIUCHI_DINGPA.get());
                        output.accept(ModItems.XIAN_DAN.get());
                        output.accept(ModItems.MINI_BAGUA_FURNACE.get());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }
}
