package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterEntity;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import icu.ytlsnb.ytls.gameplay.plunger.entity.PlungerHookEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@RegisterEntity("plunger_hook")
public final class PlungerHookEntitySupplier implements RegistrySupplier<EntityType<?>> {
    @Override
    public EntityType<?> get() {
        return EntityType.Builder.<PlungerHookEntity>of(PlungerHookEntity::new, MobCategory.MISC)
                .sized(0.4F, 0.4F)
                .clientTrackingRange(10)
                .updateInterval(1)
                .build("plunger_hook");
    }
}
