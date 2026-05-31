package icu.ytlsnb.ytls.event;

import icu.ytlsnb.ytls.client.ClientKeyMappings;
import icu.ytlsnb.ytls.client.render.HomelanderRenderer;
import icu.ytlsnb.ytls.client.screen.MilkAltarScreen;
import icu.ytlsnb.ytls.registry.ModEntityTypes;
import icu.ytlsnb.ytls.registry.ModMenuTypes;
import icu.ytlsnb.ytls.system.PlayerLactationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = icu.ytlsnb.ytls.ModConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientEvents {

    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.HOMELANDER.get(), HomelanderRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenuTypes.MILK_ALTAR.get(), MilkAltarScreen::new));
    }

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(ClientKeyMappings.USE_MILK_ABILITY);
        event.register(ClientKeyMappings.COLLECT_SELF_MILK);
    }

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("lactation_hud", LACTATION_OVERLAY);
    }

    private static final IGuiOverlay LACTATION_OVERLAY = (gui, graphics, partialTick, width, height) -> {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }
        int lactation = PlayerLactationManager.getLactation(minecraft.player);
        String text = "泌乳值: " + lactation + "ml / 3000ml";
        graphics.drawString(minecraft.font, text, 10, 10, 0xFFFFFF);
    };
}

@Mod.EventBusSubscriber(modid = icu.ytlsnb.ytls.ModConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
class ClientForgeEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.getConnection() == null) {
            return;
        }
        if (ClientKeyMappings.USE_MILK_ABILITY.consumeClick()) {
            minecraft.getConnection().sendCommand("ytls_use_ability");
        }
        if (ClientKeyMappings.COLLECT_SELF_MILK.consumeClick()) {
            if (minecraft.player.isShiftKeyDown()) {
                minecraft.getConnection().sendCommand("ytls_collect_self_milk");
            }
        }
    }
}
