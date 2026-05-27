package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterMobEffect;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import icu.ytlsnb.ytls.gameplay.effect.ExampleBriskEffect;
import net.minecraft.world.effect.MobEffect;

@RegisterMobEffect("example_brisk")
public final class ExampleMobEffectSupplier implements RegistrySupplier<MobEffect> {
    @Override
    public MobEffect get() {
        return new ExampleBriskEffect();
    }
}
