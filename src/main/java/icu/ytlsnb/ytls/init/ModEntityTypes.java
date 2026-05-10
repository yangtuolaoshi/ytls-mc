package icu.ytlsnb.ytls.init;

import icu.ytlsnb.ytls.entity.AIWolfEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static icu.ytlsnb.ytls.core.ModConstants.MOD_ID;

public final class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<AIWolfEntity>> AI_WOLF =
            ENTITY_TYPES.register(
                    "ai_wolf",
                    () -> EntityType.Builder.of(AIWolfEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 0.85f)
                            .clientTrackingRange(8)
                            .build("ai_wolf")
            );

    private ModEntityTypes() {
    }
}
