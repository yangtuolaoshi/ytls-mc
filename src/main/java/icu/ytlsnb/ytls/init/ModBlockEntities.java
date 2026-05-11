package icu.ytlsnb.ytls.init;

import icu.ytlsnb.ytls.block.entity.BaguaFurnaceBlockEntity;
import icu.ytlsnb.ytls.block.entity.JinguBangRodBlockEntity;
import icu.ytlsnb.ytls.block.entityblock.blockentity.ObsidianCounterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static icu.ytlsnb.ytls.core.ModConstants.MOD_ID;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<BlockEntityType<ObsidianCounterBlockEntity>> OBSIDIAN_COUNTER =
            BLOCK_ENTITY_TYPES.register(
                    "obsidian_counter",
                    () -> BlockEntityType.Builder
                            .of(ObsidianCounterBlockEntity::new, ModBlocks.OBSIDIAN_COUNTER.get())
                            .build(null)
            );

    public static final RegistryObject<BlockEntityType<BaguaFurnaceBlockEntity>> BAGUA_FURNACE =
            BLOCK_ENTITY_TYPES.register(
                    "bagua_furnace",
                    () -> BlockEntityType.Builder.of(BaguaFurnaceBlockEntity::new, ModBlocks.BAGUA_FURNACE.get()).build(null)
            );

    public static final RegistryObject<BlockEntityType<JinguBangRodBlockEntity>> JINGU_BANG_ROD =
            BLOCK_ENTITY_TYPES.register(
                    "placed_jingu_bang",
                    () -> BlockEntityType.Builder.of(JinguBangRodBlockEntity::new, ModBlocks.PLACED_JINGU_BANG.get()).build(null)
            );

    private ModBlockEntities() {
    }
}
