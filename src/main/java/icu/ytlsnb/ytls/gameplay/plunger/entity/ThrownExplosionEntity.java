package icu.ytlsnb.ytls.gameplay.plunger.entity;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/**
 * 「爆炸」物品投出的投射物：命中方块或实体后产生较大爆炸。
 */
public class ThrownExplosionEntity extends ThrowableItemProjectile {
    /** 略大于普通苦力怕（3），小于高压苦力怕（6） */
    public static final float EXPLOSION_POWER = 4.5F;

    public ThrownExplosionEntity(EntityType<? extends ThrownExplosionEntity> type, Level level) {
        super(type, level);
    }

    public ThrownExplosionEntity(Level level, LivingEntity shooter) {
        super(RegistryAccess.resolve(RegistryKind.ENTITY, "thrown_explosion"), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return RegistryAccess.item("sucked_explosion");
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; i++) {
                level().addParticle(ParticleTypes.EXPLOSION,
                        getX(), getY(), getZ(), 0, 0, 0);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            level().explode(this, getX(), getY(), getZ(), EXPLOSION_POWER, Level.ExplosionInteraction.TNT);
            level().broadcastEntityEvent(this, (byte) 3);
            discard();
        }
    }
}
