package icu.ytlsnb.ytls.registry;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.fluid.MilkFluid;
import icu.ytlsnb.ytls.fluid.MilkFluidType;
import icu.ytlsnb.ytls.milk.MilkType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public final class ModFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ModConstants.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ModConstants.MOD_ID);

    public static final RegistryObject<FluidType> MILK_FLUID_TYPE = FLUID_TYPES.register("milk", MilkFluidType::new);

    public static final Map<MilkType, RegistryObject<FlowingFluid>> SOURCE_FLUIDS = new EnumMap<>(MilkType.class);
    public static final Map<MilkType, RegistryObject<FlowingFluid>> FLOWING_FLUIDS = new EnumMap<>(MilkType.class);

    private ModFluids() {
    }

    public static void bootstrap() {
        for (MilkType milkType : MilkType.values()) {
            registerFluid(milkType);
        }
    }

    private static void registerFluid(MilkType milkType) {
        RegistryObject<FlowingFluid>[] sourceHolder = new RegistryObject[1];
        RegistryObject<FlowingFluid>[] flowingHolder = new RegistryObject[1];

        sourceHolder[0] = (RegistryObject<FlowingFluid>) (RegistryObject<?>) FLUIDS.register(milkType.id(),
            () -> new MilkFluid.Source(sourceHolder[0], flowingHolder[0], milkType));

        flowingHolder[0] = (RegistryObject<FlowingFluid>) (RegistryObject<?>) FLUIDS.register("flowing_" + milkType.id(),
            () -> new MilkFluid.Flowing(sourceHolder[0], flowingHolder[0], milkType));

        SOURCE_FLUIDS.put(milkType, sourceHolder[0]);
        FLOWING_FLUIDS.put(milkType, flowingHolder[0]);
    }

    public static BlockBehaviour.Properties milkBlockProperties() {
        return BlockBehaviour.Properties.of()
            .replaceable()
            .noCollission()
            .strength(100.0F)
            .pushReaction(PushReaction.DESTROY)
            .mapColor(MapColor.SNOW)
            .liquid();
    }

    public static LiquidBlock createMilkBlock(MilkType milkType) {
        return new LiquidBlock(SOURCE_FLUIDS.get(milkType), milkBlockProperties());
    }
}
