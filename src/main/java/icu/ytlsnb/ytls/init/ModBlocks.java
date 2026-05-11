package icu.ytlsnb.ytls.init;

import icu.ytlsnb.ytls.block.BaguaFurnaceBlock;
import icu.ytlsnb.ytls.block.JinguBangRodBlock;
import icu.ytlsnb.ytls.block.ObsidianBlock;
import icu.ytlsnb.ytls.block.entityblock.ObsidianCounter;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static icu.ytlsnb.ytls.core.ModConstants.MOD_ID;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MOD_ID);

    public static final RegistryObject<Block> OBSIDIAN_BLOCK = BLOCKS.register("obsidian_block", ObsidianBlock::new);
    public static final RegistryObject<Block> OBSIDIAN_COUNTER = BLOCKS.register(
            "obsidian_counter",
            () -> new ObsidianCounter(BlockBehaviour.Properties.of().strength(20F, 1500F))
    );

    public static final RegistryObject<Block> BAGUA_FURNACE = BLOCKS.register("bagua_furnace", BaguaFurnaceBlock::new);
    public static final RegistryObject<Block> PLACED_JINGU_BANG = BLOCKS.register("placed_jingu_bang", JinguBangRodBlock::new);

    private ModBlocks() {
    }
}
