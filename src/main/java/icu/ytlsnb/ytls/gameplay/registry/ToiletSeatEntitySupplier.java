package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterEntity;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import icu.ytlsnb.ytls.gameplay.toilet.entity.ToiletSeatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@RegisterEntity("toilet_seat")
public final class ToiletSeatEntitySupplier implements RegistrySupplier<EntityType<?>> {
    @Override
    public EntityType<?> get() {
        return EntityType.Builder.<ToiletSeatEntity>of(ToiletSeatEntity::new, MobCategory.MISC)
                .sized(0.25F, 0.25F)
                .clientTrackingRange(4)
                .updateInterval(10)
                .build("toilet_seat");
    }
}
