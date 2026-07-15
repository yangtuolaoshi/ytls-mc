package icu.ytlsnb.ytls.gameplay.client;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.gameplay.client.model.PlungerHookModel;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 实体渲染必须在 {@link EntityRenderersEvent.RegisterRenderers} 注册。
 * 在 FMLClientSetup 里 {@code EntityRenderers.register} 对联机客户端过晚，
 * 会导致 EntityRenderDispatcher 中 renderer 为 null 并崩溃。
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ToiletClientSetup {
    private ToiletClientSetup() {
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PlungerHookModel.LAYER_LOCATION, PlungerHookModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(entity("toilet_seat"), NoopRenderer::new);
        event.registerEntityRenderer(entity("plunger_hook"), PlungerHookRenderer::new);
        event.registerEntityRenderer(entity("weather_cloud"), WeatherCloudRenderer::new);
        event.registerEntityRenderer(entity("thrown_explosion"), ThrownItemRenderer::new);
    }

    @SuppressWarnings("unchecked")
    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> entity(String name) {
        return (EntityType<T>) RegistryAccess.resolve(RegistryKind.ENTITY, name);
    }
}
