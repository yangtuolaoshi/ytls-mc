package icu.ytlsnb.ytls.gameplay.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import icu.ytlsnb.ytls.ModConstants;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * 马桶塞枪飞行钩模型（Blockbench 导出，已适配本模组包名与图层 ID）。
 */
public class PlungerHookModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "plunger_hook"), "main");

    private final ModelPart bbMain;

    public PlungerHookModel(ModelPart root) {
        this.bbMain = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                        .texOffs(36, 23).addBox(-4.0F, -2.0F, -7.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 25).addBox(6.0F, -2.0F, -4.0F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(36, 26).addBox(-4.0F, -2.0F, 6.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(18, 25).addBox(-7.0F, -2.0F, -4.0F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 14).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 14).addBox(4.0F, -5.0F, -3.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(36, 29).addBox(-3.0F, -5.0F, -5.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(38, 33).addBox(-3.0F, -5.0F, 4.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 35).addBox(-5.0F, -5.0F, -3.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(22, 35).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 41).addBox(-1.0F, -7.0F, -3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(38, 37).addBox(2.0F, -7.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(34, 41).addBox(-1.0F, -7.0F, 2.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(22, 41).addBox(-3.0F, -7.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 35).addBox(-1.0F, -22.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        // 飞行钩无动画
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        bbMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
