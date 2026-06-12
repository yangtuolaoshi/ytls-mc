package icu.ytlsnb.ytls.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;

public class MilkRainStreakParticle extends TextureSheetParticle {

    protected MilkRainStreakParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.rCol = 1.0F;
        this.gCol = 0.97F;
        this.bCol = 0.90F;

        this.quadSize = 1.0F + random.nextFloat() * 0.06F;
        this.lifetime = 40 + random.nextInt(8);
        this.gravity = 0.0F;
        this.friction = 0.98F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
