package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterEntity;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import icu.ytlsnb.ytls.gameplay.plunger.entity.ThrownExplosionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@RegisterEntity("thrown_explosion")
public final class ThrownExplosionEntitySupplier implements RegistrySupplier<EntityType<?>> {
    @Override
    public EntityType<?> get() {
        return EntityType.Builder.<ThrownExplosionEntity>of(ThrownExplosionEntity::new, MobCategory.MISC)
                .sized(0.25F, 0.25F)
                .clientTrackingRange(4)
                .updateInterval(10)
                .build("thrown_explosion");
    }
}
