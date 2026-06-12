package icu.ytlsnb.ytls.registry;

import icu.ytlsnb.ytls.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModConstants.MOD_ID);

    public static final RegistryObject<CreativeModeTab> YTLS_TAB = CREATIVE_MODE_TABS.register("ytls_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ytls.main"))
            .icon(() -> new net.minecraft.world.item.ItemStack(ModItems.MILK_BUCKETS.get(icu.ytlsnb.ytls.milk.MilkType.COW).get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.MILK_ALTAR_ITEM.get());
                output.accept(ModItems.GALACTAGOGUE.get());
                ModItems.MILK_BUCKETS.values().forEach(item -> output.accept(item.get()));
                output.accept(ModSpawnEggs.HOMELANDER_SPAWN_EGG.get());
            })
            .build());

    private ModCreativeTabs() {
    }
}
