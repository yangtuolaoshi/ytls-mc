package icu.ytlsnb.ytls.event;

import icu.ytlsnb.ytls.client.ClientKeyMappings;
import icu.ytlsnb.ytls.client.MilkRainClientState;
import icu.ytlsnb.ytls.client.particle.MilkDropProvider;
import icu.ytlsnb.ytls.client.particle.MilkRainSplashProvider;
import icu.ytlsnb.ytls.client.particle.MilkRainStreakProvider;
import icu.ytlsnb.ytls.client.render.HomelanderRenderer;
import icu.ytlsnb.ytls.client.screen.MilkAltarScreen;
import icu.ytlsnb.ytls.milk.MilkType;
import icu.ytlsnb.ytls.registry.ModEntityTypes;
import icu.ytlsnb.ytls.registry.ModMenuTypes;
import icu.ytlsnb.ytls.registry.ModParticles;
import icu.ytlsnb.ytls.system.MilkAbilityManager;
import icu.ytlsnb.ytls.system.PlayerLactationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent;
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
        event.registerAboveAll("milk_rain_dimmer", MILK_RAIN_DIMMER);
        event.registerAboveAll("lactation_hud", LACTATION_OVERLAY);
    }

    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.MILK_DROP.get(), MilkDropProvider::new);
        event.registerSpriteSet(ModParticles.MILK_RAIN_STREAK.get(), MilkRainStreakProvider::new);
        event.registerSpriteSet(ModParticles.MILK_RAIN_SPLASH.get(), MilkRainSplashProvider::new);
    }

    private static final IGuiOverlay LACTATION_OVERLAY = (gui, graphics, partialTick, width, height) -> {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }
        int lactation = PlayerLactationManager.getLactation(minecraft.player);
        String text = "泌乳值: " + lactation + "ml / 3000ml";
        graphics.drawString(minecraft.font, text, 10, 10, 0xFFFFFF);

        int y = 22;
        y = drawAbilityTime(graphics, minecraft, MilkType.DRAGON, "龙息", y);
        drawAbilityTime(graphics, minecraft, MilkType.WITHER, "凋零头颅", y);
    };

    private static int drawAbilityTime(GuiGraphics graphics, Minecraft minecraft, MilkType type, String label, int y) {
        int remainingTicks = MilkAbilityManager.getRemainingTicks(minecraft.player, type);
        if (remainingTicks <= 0) {
            return y;
        }
        int tenths = (remainingTicks + 1) * 10 / 20;
        String text = label + "剩余: " + (tenths / 10) + "." + (tenths % 10) + "秒";
        graphics.drawString(minecraft.font, text, 10, y, 0xFFEFE8D8);
        return y + 12;
    }

    private static final IGuiOverlay MILK_RAIN_DIMMER = (gui, graphics, partialTick, width, height) -> {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.options.hideGui) {
            return;
        }
        if (!MilkRainClientState.isActive()) {
            return;
        }
        graphics.fill(0, 0, width, height, 0x4A808080);
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
                PlayerLactationManager.consumeForBucket(minecraft.player);
                minecraft.getConnection().sendCommand("ytls_collect_self_milk");
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || minecraft.isPaused()) {
            return;
        }
        MilkRainClientState.tick();
        if (!MilkRainClientState.isActive()) {
            return;
        }

        if (minecraft.player.tickCount % 18 == 0) {
            double sx = minecraft.player.getX() + (minecraft.level.random.nextDouble() - 0.5D) * 6.0D;
            double sy = minecraft.player.getY() + 1.0D;
            double sz = minecraft.player.getZ() + (minecraft.level.random.nextDouble() - 0.5D) * 6.0D;
            minecraft.level.playLocalSound(sx, sy, sz, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.35F, 1.0F, false);
        }
    }
}
