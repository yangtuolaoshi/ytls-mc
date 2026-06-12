package icu.ytlsnb.ytls.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class MilkDropProvider implements ParticleProvider<SimpleParticleType> {

    private final SpriteSet sprites;

    public MilkDropProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        MilkDropParticle particle = new MilkDropParticle(level, x, y, z, xd, yd, zd);
        particle.pickSprite(sprites);
        return particle;
    }
}
