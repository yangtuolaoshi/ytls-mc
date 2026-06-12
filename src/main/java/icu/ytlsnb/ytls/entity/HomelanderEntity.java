package icu.ytlsnb.ytls.entity;

import icu.ytlsnb.ytls.system.MilkWorldSystems;
import icu.ytlsnb.ytls.registry.ModSounds;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class HomelanderEntity extends TamableAnimal {

    private static final EntityDataAccessor<Boolean> DATA_ANGRY = SynchedEntityData.defineId(HomelanderEntity.class, EntityDataSerializers.BOOLEAN);
    private int heatVisionCooldown;

    public HomelanderEntity(EntityType<? extends HomelanderEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 80.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            .add(Attributes.ATTACK_DAMAGE, 14.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ANGRY, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.1D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new MilkSeekGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) {
            if (heatVisionCooldown > 0) {
                heatVisionCooldown--;
            }
            LivingEntity target = getTarget();
            if (target != null && hasLineOfSight(target) && distanceTo(target) > 3.0F && heatVisionCooldown == 0) {
                performHeatVision(target);
                heatVisionCooldown = 50;
            }
            if (random.nextInt(240) == 0) {
                level().playSound(null, blockPosition(), ModSounds.HOMELANDER_AMBIENT.get(), getSoundSource(), 0.8F, 0.8F + random.nextFloat() * 0.4F);
            }
        }
    }

    private void performHeatVision(LivingEntity target) {
        target.hurt(damageSources().mobAttack(this), 16.0F);
        target.setSecondsOnFire(4);
        level().playSound(null, blockPosition(), ModSounds.HOMELANDER_HEAT_VISION.get(), getSoundSource(), 1.0F, 0.9F + random.nextFloat() * 0.2F);
        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 12; i++) {
                double t = i / 12.0D;
                double x = Mth.lerp(t, getX(), target.getX());
                double y = Mth.lerp(t, getEyeY(), target.getEyeY());
                double z = Mth.lerp(t, getZ(), target.getZ());
                serverLevel.sendParticles(new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.2F), x, y, z, 1, 0, 0, 0, 0.01D);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean isMilkBucket = MilkWorldSystems.isAnyMilkBucket(stack);
        if (isMilkBucket) {
            if (!level().isClientSide) {
                this.heal(6.0F);
                if (MilkWorldSystems.isPlayerMilkBucket(stack) && !isTame()) {
                    tame(player);
                    setOrderedToSit(false);
                    level().playSound(null, blockPosition(), ModSounds.HOMELANDER_TAME.get(), getSoundSource(), 1.0F, 1.0F);
                    level().broadcastEntityEvent(this, (byte) 7);
                }
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    player.addItem(new ItemStack(net.minecraft.world.item.Items.BUCKET));
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    public void setAngryAt(Player player) {
        setTarget(player);
        entityData.set(DATA_ANGRY, true);
    }

    public boolean isAngryAt(Player player) {
        return entityData.get(DATA_ANGRY) && getTarget() == player;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(DATA_ANGRY, tag.getBoolean("IsAngry"));
        heatVisionCooldown = tag.getInt("HeatVisionCooldown");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsAngry", entityData.get(DATA_ANGRY));
        tag.putInt("HeatVisionCooldown", heatVisionCooldown);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        return null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.HOMELANDER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.HOMELANDER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.HOMELANDER_DEATH.get();
    }
}
