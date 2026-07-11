package icu.ytlsnb.ytls.gameplay.client;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.network.NetworkAccess;
import icu.ytlsnb.ytls.gameplay.network.TogglePlungerSoundModeMessage;
import icu.ytlsnb.ytls.gameplay.plunger.item.PlungerItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, value = Dist.CLIENT)
public final class PlungerKeyBindings {
    public static final KeyMapping TOGGLE_SOUND_MODE = new KeyMapping(
            "key.ytls.plunger_sound_mode",
            GLFW.GLFW_KEY_V,
            "key.categories.ytls"
    );

    private PlungerKeyBindings() {
    }

    @Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Register {
        private Register() {
        }

        @SubscribeEvent
        public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_SOUND_MODE);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.screen != null) {
            return;
        }
        while (TOGGLE_SOUND_MODE.consumeClick()) {
            if (!(player.getMainHandItem().getItem() instanceof PlungerItem)
                    && !(player.getOffhandItem().getItem() instanceof PlungerItem)) {
                continue;
            }
            NetworkAccess.channel().sendToServer(new TogglePlungerSoundModeMessage());
        }
    }
}
