package icu.ytlsnb.ytls.client.render;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.entity.HomelanderEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HomelanderRenderer extends HumanoidMobRenderer<HomelanderEntity, PlayerModel<HomelanderEntity>> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/entity/homelander.png");

    public HomelanderRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(HomelanderEntity entity) {
        return TEXTURE;
    }
}
