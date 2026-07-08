package icu.ytlsnb.ytls.registry;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.block.MilkAltarBlock;
import icu.ytlsnb.ytls.milk.MilkType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModConstants.MOD_ID);

    public static final Map<MilkType, RegistryObject<LiquidBlock>> MILK_BLOCKS = new EnumMap<>(MilkType.class);

    public static final RegistryObject<Block> MILK_ALTAR = BLOCKS.register("milk_altar", () -> new MilkAltarBlock(
        BlockBehaviour.Properties.of()
            .strength(4.0F, 6.0F)
            .mapColor(MapColor.COLOR_CYAN)
            .requiresCorrectToolForDrops()
            .noOcclusion()
    ));

    private ModBlocks() {
    }

    public static void bootstrap() {
        for (MilkType type : MilkType.values()) {
            RegistryObject<LiquidBlock> block = BLOCKS.register(type.id(), () -> ModFluids.createMilkBlock(type));
            MILK_BLOCKS.put(type, block);
        }
    }
}
