package icu.ytlsnb.ytls.registry;

import icu.ytlsnb.ytls.ModConstants;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
        DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ModConstants.MOD_ID);

    public static final RegistryObject<SimpleParticleType> MILK_DROP =
        PARTICLES.register("milk_drop", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MILK_RAIN_STREAK =
        PARTICLES.register("milk_rain_streak", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MILK_RAIN_SPLASH =
        PARTICLES.register("milk_rain_splash", () -> new SimpleParticleType(true));

    private ModParticles() {
    }
}
