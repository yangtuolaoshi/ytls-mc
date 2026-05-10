package icu.ytlsnb.ytls.init;

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

    private ModBlockEntities() {
    }
}
