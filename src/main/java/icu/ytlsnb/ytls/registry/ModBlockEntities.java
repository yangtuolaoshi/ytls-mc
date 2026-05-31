package icu.ytlsnb.ytls.registry;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.blockentity.MilkAltarBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModConstants.MOD_ID);

    public static final RegistryObject<BlockEntityType<MilkAltarBlockEntity>> MILK_ALTAR = BLOCK_ENTITY_TYPES.register("milk_altar",
        () -> BlockEntityType.Builder.of(MilkAltarBlockEntity::new, ModBlocks.MILK_ALTAR.get()).build(null));

    private ModBlockEntities() {
    }
}
