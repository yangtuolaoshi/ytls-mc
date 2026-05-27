package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterParticle;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

@RegisterParticle("example_sparkle")
public final class ExampleParticleSupplier implements RegistrySupplier<ParticleType<?>> {
    @Override
    public SimpleParticleType get() {
        return new SimpleParticleType(true);
    }
}
