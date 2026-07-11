package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterCreativeTab;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

@RegisterCreativeTab("toilet_tab")
public final class ToiletCreativeTabSupplier implements RegistrySupplier<CreativeModeTab> {
    @Override
    public CreativeModeTab get() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + ModConstants.MOD_ID + ".toilet_tab"))
                .icon(() -> new ItemStack(RegistryAccess.item("plunger")))
                .displayItems((params, output) -> {
                    output.accept(RegistryAccess.item("toilet"));
                    output.accept(RegistryAccess.item("plunger"));
                    output.accept(RegistryAccess.item("plunger_gun"));
                    output.accept(RegistryAccess.item("poop"));
                    output.accept(RegistryAccess.item("rain_cloud"));
                    output.accept(RegistryAccess.item("thunder_cloud"));
                    output.accept(RegistryAccess.item("sucked_fireball"));
                    output.accept(RegistryAccess.item("sucked_explosion"));
                })
                .build();
    }
}
