package icu.ytlsnb.ytls.fluid;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.milk.MilkType;
import icu.ytlsnb.ytls.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public abstract class MilkFluid extends FlowingFluid {

    protected final RegistryObject<? extends Fluid> source;
    protected final RegistryObject<? extends Fluid> flowing;
    protected final MilkType milkType;

    protected MilkFluid(RegistryObject<? extends Fluid> source,
                        RegistryObject<? extends Fluid> flowing,
                        MilkType milkType) {
        this.source = source;
        this.flowing = flowing;
        this.milkType = milkType;
    }

    @Override
    public Fluid getFlowing() {
        return flowing.get();
    }

    @Override
    public Fluid getSource() {
        return source.get();
    }

    @Override
    public Item getBucket() {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModConstants.MOD_ID, milkType.id() + "_bucket"));
        return item == null ? Items.BUCKET : item;
    }

    @Override
    public FluidType getFluidType() {
        return ModFluids.MILK_FLUID_TYPE.get();
    }

    public MilkType getMilkType() {
        return milkType;
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor accessor, BlockPos blockPos, BlockState blockState) {
        // 牛奶流体不需要额外掉落逻辑
    }

    @Override
    protected int getSlopeFindDistance(LevelReader levelReader) {
        return 3;
    }

    @Override
    protected int getDropOff(LevelReader levelReader) {
        return 1;
    }

    @Override
    public int getTickDelay(LevelReader levelReader) {
        return 8;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ModConstants.MOD_ID, milkType.id()));
        if (block == null) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return block.defaultBlockState()
            .setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == source.get() || fluid == flowing.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, net.minecraft.core.Direction direction) {
        return false;
    }

    public static class Source extends MilkFluid {
        public Source(RegistryObject<? extends Fluid> source,
                      RegistryObject<? extends Fluid> flowing,
                      MilkType milkType) {
            super(source, flowing, milkType);
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return true;
        }
    }

    public static class Flowing extends MilkFluid {
        public Flowing(RegistryObject<? extends Fluid> source,
                       RegistryObject<? extends Fluid> flowing,
                       MilkType milkType) {
            super(source, flowing, milkType);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override
        protected void createFluidStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return fluidState.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return false;
        }
    }

}
