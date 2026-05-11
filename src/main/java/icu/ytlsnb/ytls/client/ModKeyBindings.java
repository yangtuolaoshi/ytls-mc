package icu.ytlsnb.ytls.client;

import icu.ytlsnb.ytls.core.ModConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, value = net.minecraftforge.api.distmarker.Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModKeyBindings {
    public static final KeyMapping BAGUA_ACTION = new KeyMapping(
            "key.ytls.bagua_action",
            GLFW.GLFW_KEY_R,
            "key.categories.ytls"
    );

    private ModKeyBindings() {
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(BAGUA_ACTION);
    }
}
