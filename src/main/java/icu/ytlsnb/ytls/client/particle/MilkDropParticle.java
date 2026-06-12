package icu.ytlsnb.ytls.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;

public class MilkDropParticle extends TextureSheetParticle {

    protected MilkDropParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.rCol = 1.0F;
        this.gCol = 0.98F;
        this.bCol = 0.92F;

        this.quadSize = 0.08F + random.nextFloat() * 0.05F;
        this.lifetime = 20 + random.nextInt(10);
        this.gravity = 0.12F;
        this.friction = 0.98F;
    }

    @Override
    public void tick() {
        super.tick();
        this.xd += Math.sin(this.age * 0.2F) * 0.0008D;
        if (this.onGround) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
