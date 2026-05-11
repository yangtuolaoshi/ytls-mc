package icu.ytlsnb.ytls.client;

import icu.ytlsnb.ytls.client.gui.BaguaFurnaceScreen;
import icu.ytlsnb.ytls.client.gui.MiniBaguaScreen;
import icu.ytlsnb.ytls.core.ModConstants;
import icu.ytlsnb.ytls.init.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientMenuScreens {
    private ClientMenuScreens() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.BAGUA_FURNACE.get(), BaguaFurnaceScreen::new);
            MenuScreens.register(ModMenus.MINI_BAGUA.get(), MiniBaguaScreen::new);
        });
    }
}
