package icu.ytlsnb.ytls.init;

import icu.ytlsnb.ytls.world.inventory.BaguaFurnaceMenu;
import icu.ytlsnb.ytls.world.inventory.MiniBaguaMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static icu.ytlsnb.ytls.core.ModConstants.MOD_ID;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MOD_ID);

    public static final RegistryObject<MenuType<BaguaFurnaceMenu>> BAGUA_FURNACE =
            MENUS.register(
                    "bagua_furnace",
                    () -> IForgeMenuType.create(BaguaFurnaceMenu::fromNetwork)
            );

    public static final RegistryObject<MenuType<MiniBaguaMenu>> MINI_BAGUA =
            MENUS.register(
                    "mini_bagua",
                    () -> IForgeMenuType.create(MiniBaguaMenu::fromNetwork)
            );

    private ModMenus() {
    }
}
