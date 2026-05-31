package icu.ytlsnb.ytls.registry;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.entity.HomelanderEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModConstants.MOD_ID);

    public static final RegistryObject<EntityType<HomelanderEntity>> HOMELANDER = ENTITY_TYPES.register("homelander",
        () -> EntityType.Builder.of(HomelanderEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(10)
            .build(ModConstants.MOD_ID + ":homelander"));

    private ModEntityTypes() {
    }
}
