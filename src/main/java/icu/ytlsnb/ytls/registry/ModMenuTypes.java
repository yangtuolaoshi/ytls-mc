package icu.ytlsnb.ytls.registry;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.menu.MilkAltarMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ModConstants.MOD_ID);

    public static final RegistryObject<MenuType<MilkAltarMenu>> MILK_ALTAR = MENUS.register("milk_altar",
        () -> IForgeMenuType.create(MilkAltarMenu::new));

    private ModMenuTypes() {
    }
}
