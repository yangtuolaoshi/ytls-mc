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
import net.minecraft.world.entity.HumanoidArm;
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
        // 略缩小，减少贴地时模型穿进方块
        poseStack.translate(0.0D, 0.05D, 0.0D);
        poseStack.scale(0.85F, 0.85F, 0.85F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
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
        float handOffset = owner.getMainArm() == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        double ownerX = Mth.lerp(partialTicks, owner.xo, owner.getX());
        double ownerY = Mth.lerp(partialTicks, owner.yo, owner.getY()) + owner.getEyeHeight() * 0.7D;
        double ownerZ = Mth.lerp(partialTicks, owner.zo, owner.getZ());

        float yaw = Mth.lerp(partialTicks, owner.yRotO, owner.getYRot()) * ((float) Math.PI / 180F);
        double sideX = Mth.cos(yaw) * handOffset * 0.35D;
        double sideZ = Mth.sin(yaw) * handOffset * 0.35D;
        Vec3 hand = new Vec3(ownerX + sideX, ownerY, ownerZ + sideZ);
        Vec3 hookPos = hook.getPosition(partialTicks);

        float dx = (float) (hand.x - hookPos.x);
        float dy = (float) (hand.y - hookPos.y);
        float dz = (float) (hand.z - hookPos.z);
        float span = Mth.sqrt(dx * dx + dy * dy + dz * dz);
        // 下垂幅度随距离缩放，且不超过两端最低点，避免贴地时线穿进地面
        float maxSag = Math.min(0.2F, span * 0.08F);
        float floorY = Math.min(0.0F, dy) + 0.02F;

        VertexConsumer line = buffer.getBuffer(RenderType.lines());
        Matrix4f matrix = poseStack.last().pose();
        int segments = 24;
        for (int i = 0; i < segments; i++) {
            float t0 = i / (float) segments;
            float t1 = (i + 1) / (float) segments;
            float y0 = clampLineY(dy * t0, Mth.sin(t0 * (float) Math.PI) * maxSag, floorY);
            float y1 = clampLineY(dy * t1, Mth.sin(t1 * (float) Math.PI) * maxSag, floorY);
            line.vertex(matrix, dx * t0, y0, dz * t0)
                    .color(70, 50, 30, 255)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            line.vertex(matrix, dx * t1, y1, dz * t1)
                    .color(70, 50, 30, 255)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
        }
    }

    private static float clampLineY(float baseY, float sag, float floorY) {
        return Math.max(baseY - sag, floorY);
    }

    @Override
    public ResourceLocation getTextureLocation(PlungerHookEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/fishing_hook.png");
    }
}
