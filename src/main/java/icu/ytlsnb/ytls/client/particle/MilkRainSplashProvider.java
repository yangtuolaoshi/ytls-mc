package icu.ytlsnb.ytls.client.particle;

import icu.ytlsnb.ytls.client.MilkRainClientState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class MilkRainSplashProvider implements ParticleProvider<SimpleParticleType> {

    private final SpriteSet sprites;

    public MilkRainSplashProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        MilkRainClientState.markActive();
        MilkRainSplashParticle particle = new MilkRainSplashParticle(level, x, y, z, xd, yd, zd);
        particle.pickSprite(sprites);
        return particle;
    }
}
