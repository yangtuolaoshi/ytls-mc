package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterCreativeTab;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

@RegisterCreativeTab("example_tab")
public final class ExampleCreativeTabSupplier implements RegistrySupplier<CreativeModeTab> {
    @Override
    public CreativeModeTab get() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + ModConstants.MOD_ID + ".example_tab"))
                .icon(() -> new ItemStack(RegistryAccess.item("example_ingot")))
                .displayItems((params, output) -> output.accept(RegistryAccess.item("example_ingot")))
                .build();
    }
}
