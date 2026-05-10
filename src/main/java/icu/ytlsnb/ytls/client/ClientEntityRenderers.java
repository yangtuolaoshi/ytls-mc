package icu.ytlsnb.ytls.client;

import icu.ytlsnb.ytls.core.ModConstants;
import icu.ytlsnb.ytls.entity.AIWolfRenderer;
import icu.ytlsnb.ytls.init.ModEntityTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientEntityRenderers {
    private ClientEntityRenderers() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.AI_WOLF.get(), AIWolfRenderer::new);
    }
}
