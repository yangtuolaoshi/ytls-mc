package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterEntity;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import icu.ytlsnb.ytls.gameplay.plunger.entity.WeatherCloudEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@RegisterEntity("weather_cloud")
public final class WeatherCloudEntitySupplier implements RegistrySupplier<EntityType<?>> {
    @Override
    public EntityType<?> get() {
        return EntityType.Builder.<WeatherCloudEntity>of(WeatherCloudEntity::new, MobCategory.MISC)
                .sized(1.2F, 0.6F)
                .clientTrackingRange(10)
                .updateInterval(2)
                .fireImmune()
                .build("weather_cloud");
    }
}
