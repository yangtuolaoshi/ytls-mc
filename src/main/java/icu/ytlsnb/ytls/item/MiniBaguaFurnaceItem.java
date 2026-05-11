package icu.ytlsnb.ytls.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 迷你八卦炉：燃料存 NBT；按住右键持续魔炮；每 10 tick 消耗各类已有蘑菇各 1。
 */
public class MiniBaguaFurnaceItem extends Item {
    public static final String TAG_RED = "FuelRed";
    public static final String TAG_BROWN = "FuelBrown";
    public static final String TAG_WARPED = "FuelWarped";
    public static final String TAG_CRIMSON = "FuelCrimson";

    private static final int USE_DURATION = 72000;
    private static final int CONSUME_INTERVAL = 10;
    private static final float BASE_DAMAGE = 2.0F;
    private static final double REACH = 10.0;
    private static final double BEAM_RADIUS = 1.4;

    public MiniBaguaFurnaceItem() {
        super(new Properties().stacksTo(1).fireResistant());
    }

    public static int getFuel(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(key) : 0;
    }

    public static void setFuel(ItemStack stack, String key, int amount) {
        stack.getOrCreateTag().putInt(key, Math.max(0, amount));
    }

    public static int countActiveKinds(ItemStack stack) {
        int n = 0;
        if (getFuel(stack, TAG_RED) > 0) {
            n++;
        }
        if (getFuel(stack, TAG_BROWN) > 0) {
            n++;
        }
        if (getFuel(stack, TAG_WARPED) > 0) {
            n++;
        }
        if (getFuel(stack, TAG_CRIMSON) > 0) {
            n++;
        }
        return n;
    }

    public static boolean hasAnyFuel(ItemStack stack) {
        return countActiveKinds(stack) > 0;
    }

    public static void consumeFuelCycle(ItemStack stack) {
        consumeOne(stack, TAG_RED);
        consumeOne(stack, TAG_BROWN);
        consumeOne(stack, TAG_WARPED);
        consumeOne(stack, TAG_CRIMSON);
    }

    private static void consumeOne(ItemStack stack, String key) {
        int v = getFuel(stack, key);
        if (v > 0) {
            setFuel(stack, key, v - 1);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!hasAnyFuel(stack)) {
            return InteractionResultHolder.pass(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide) {
            spawnClientParticles(level, entity, stack);
            return;
        }
        if (!(entity instanceof Player player)) {
            return;
        }
        if (!hasAnyFuel(stack)) {
            player.stopUsingItem();
            return;
        }
        int elapsed = USE_DURATION - remainingUseDuration;
        if (elapsed > 0 && elapsed % CONSUME_INTERVAL == 0) {
            consumeFuelCycle(stack);
        }
        if (!hasAnyFuel(stack)) {
            player.stopUsingItem();
            return;
        }
        tickBeamServer(level, player, stack);
    }

    private static void tickBeamServer(Level level, Player player, ItemStack stack) {
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle().normalize();
        float dmg = BASE_DAMAGE + countActiveKinds(stack) * 2.0F;

        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(REACH + 2));
        int gt = (int) (level.getGameTime() % 40);
        boolean redBurst = getFuel(stack, TAG_RED) > 0 && gt % 12 == 0;
        boolean warpStrike = getFuel(stack, TAG_WARPED) > 0 && gt % 15 == 0;

        for (LivingEntity target : nearby) {
            if (target == player || !target.isAlive()) {
                continue;
            }
            if (!isInBeam(eye, look, target.position().add(0, target.getBbHeight() * 0.5, 0))) {
                continue;
            }

            target.hurt(level.damageSources().magic(), dmg);

            if (getFuel(stack, TAG_BROWN) > 0) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
            }
            if (getFuel(stack, TAG_CRIMSON) > 0) {
                target.setSecondsOnFire(2);
            }
            if (redBurst) {
                target.hurt(level.damageSources().magic(), 2.0F);
                target.setRemainingFireTicks(target.getRemainingFireTicks() + 20);
            }
            if (warpStrike) {
                target.hurt(level.damageSources().lightningBolt(), 3.0F);
            }
        }
    }

    private static boolean isInBeam(Vec3 eye, Vec3 look, Vec3 targetMid) {
        Vec3 rel = targetMid.subtract(eye);
        double proj = rel.dot(look);
        if (proj < 0 || proj > REACH) {
            return false;
        }
        Vec3 closest = eye.add(look.scale(proj));
        double distSq = targetMid.distanceToSqr(closest);
        double kindsBonus = BEAM_RADIUS;
        return distSq <= kindsBonus * kindsBonus;
    }

    private static void spawnClientParticles(Level level, LivingEntity entity, ItemStack stack) {
        if (!(entity instanceof Player player)) {
            return;
        }
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();
        RandomSource rnd = level.random;
        if (!hasAnyFuel(stack)) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            double ox = look.x * (0.5 + rnd.nextDouble());
            double oy = look.y * (0.5 + rnd.nextDouble());
            double oz = look.z * (0.5 + rnd.nextDouble());
            if (getFuel(stack, TAG_WARPED) > 0) {
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, eye.x + ox, eye.y + oy, eye.z + oz, 0, 0, 0);
            }
            if (getFuel(stack, TAG_RED) > 0) {
                level.addParticle(ParticleTypes.FLAME, eye.x + ox, eye.y + oy, eye.z + oz, 0.02, 0.02, 0.02);
            }
            if (getFuel(stack, TAG_BROWN) > 0) {
                level.addParticle(ParticleTypes.MYCELIUM, eye.x + ox, eye.y + oy, eye.z + oz, 0, 0.02, 0);
            }
            if (getFuel(stack, TAG_CRIMSON) > 0) {
                level.addParticle(ParticleTypes.SMOKE, eye.x + ox, eye.y + oy, eye.z + oz, 0.01, 0.05, 0.01);
            }
        }
    }
}
