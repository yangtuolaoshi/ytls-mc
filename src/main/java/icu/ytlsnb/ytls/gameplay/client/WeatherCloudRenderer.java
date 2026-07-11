package icu.ytlsnb.ytls.gameplay.client;

import icu.ytlsnb.ytls.gameplay.plunger.entity.WeatherCloudEntity;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 天气云主要靠粒子表现，实体本身跳过模型渲染。
 */
public class WeatherCloudRenderer extends EntityRenderer<WeatherCloudEntity> {
    private static final ResourceLocation TEXTURE = ModResources.loc("textures/entity/weather_cloud.png");

    public WeatherCloudRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(WeatherCloudEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        // 仍参与粒子 tick；渲染体可跳过以减少遮挡
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(WeatherCloudEntity entity) {
        return TEXTURE;
    }
}
