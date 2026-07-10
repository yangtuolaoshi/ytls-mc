package icu.ytlsnb.ytls.gameplay.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.gameplay.client.model.PlungerHookModel;
import icu.ytlsnb.ytls.gameplay.plunger.entity.PlungerHookEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class PlungerHookRenderer extends EntityRenderer<PlungerHookEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/entity/plunger_hook.png");

    private final PlungerHookModel<PlungerHookEntity> model;

    public PlungerHookRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PlungerHookModel<>(context.bakeLayer(PlungerHookModel.LAYER_LOCATION));
    }

    @Override
    public void render(PlungerHookEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.1D, 0.0D);
        // 相对飞行朝向再转 180°，纠正模型前后颠倒
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) + 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        // Blockbench 根节点 offset Y=24 → 1.5 格，拉回实体原点附近
        poseStack.translate(0.0D, -1.5D, 0.0D);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
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
        return TEXTURE;
    }
}
