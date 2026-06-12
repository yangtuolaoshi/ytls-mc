package icu.ytlsnb.ytls.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;

public class MilkRainSplashParticle extends TextureSheetParticle {

    protected MilkRainSplashParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.rCol = 1.0F;
        this.gCol = 0.97F;
        this.bCol = 0.90F;

        this.quadSize = 0.12F + random.nextFloat() * 0.04F;
        this.lifetime = 6 + random.nextInt(4);
        this.gravity = 0.18F;
        this.friction = 0.85F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
