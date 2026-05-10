package icu.ytlsnb.ytls.entity;

import icu.ytlsnb.ytls.core.ModResources;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AIWolfRenderer extends HumanoidMobRenderer<AIWolfEntity, HumanoidModel<AIWolfEntity>> {

    public AIWolfRenderer(EntityRendererProvider.Context context) {
        super(context,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)),
                0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(AIWolfEntity entity) {
        return ModResources.loc("textures/entity/ai_wolf.png");
    }
}