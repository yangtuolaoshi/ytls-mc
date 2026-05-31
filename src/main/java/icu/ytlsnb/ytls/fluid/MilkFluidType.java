package icu.ytlsnb.ytls.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class MilkFluidType extends FluidType {
    private static final ResourceLocation STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
    private static final ResourceLocation FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");

    public MilkFluidType() {
        super(FluidType.Properties.create()
            .density(1125)
            .viscosity(1450)
            .temperature(300)
            .canDrown(true)
            .canHydrate(true)
            .motionScale(0.012D)
            .fallDistanceModifier(0.6F)
            .adjacentPathType(BlockPathTypes.WATER)
            .pathType(BlockPathTypes.WATER));
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING_TEXTURE;
            }

            @Override
            public int getTintColor() {
                // 轻微偏乳白，避免与纯水完全相同
                return 0xFFEFE8D8;
            }
        });
    }
}
