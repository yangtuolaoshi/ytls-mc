package icu.ytlsnb.ytls.gameplay.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import icu.ytlsnb.ytls.gameplay.plunger.entity.PlungerHookEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class PlungerHookRenderer extends EntityRenderer<PlungerHookEntity> {
    public PlungerHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(PlungerHookEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 45.0F));
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(entity.getDisplayedItem(), ItemDisplayContext.FIXED, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();

        Player owner = entity.getGunOwner();
        if (owner != null) {
            renderLine(entity, partialTicks, poseStack, buffer, owner);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void renderLine(PlungerHookEntity hook, float partialTicks, PoseStack poseStack,
                                   MultiBufferSource buffer, Player owner) {
        Vec3 hookPos = hook.getPosition(partialTicks);
        double ownerX = Mth.lerp(partialTicks, owner.xo, owner.getX());
        double ownerY = Mth.lerp(partialTicks, owner.yo, owner.getY()) + owner.getEyeHeight() * 0.65D;
        double ownerZ = Mth.lerp(partialTicks, owner.zo, owner.getZ());

        float dx = (float) (ownerX - hookPos.x);
        float dy = (float) (ownerY - hookPos.y);
        float dz = (float) (ownerZ - hookPos.z);

        poseStack.pushPose();
        VertexConsumer line = buffer.getBuffer(RenderType.lineStrip());
        Matrix4f matrix = poseStack.last().pose();
        int segments = 20;
        for (int i = 0; i <= segments; i++) {
            float t = i / (float) segments;
            float sag = Mth.sin(t * (float) Math.PI) * 0.25F * (1.0F - t);
            line.vertex(matrix, dx * t, dy * t - sag, dz * t)
                    .color(60, 45, 30, 255)
                    .normal(0.0F, 1.0F, 0.0F);
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(PlungerHookEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/fishing_hook.png");
    }
}
