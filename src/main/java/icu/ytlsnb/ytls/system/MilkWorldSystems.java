package icu.ytlsnb.ytls.system;

import icu.ytlsnb.ytls.entity.HomelanderEntity;
import icu.ytlsnb.ytls.milk.MilkType;
import icu.ytlsnb.ytls.registry.ModEntityTypes;
import icu.ytlsnb.ytls.registry.ModFluids;
import icu.ytlsnb.ytls.registry.ModItems;
import icu.ytlsnb.ytls.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import java.util.Set;

public final class MilkWorldSystems {
    private static final DustParticleOptions MILK_DROP_PARTICLE = new DustParticleOptions(new Vector3f(0.97F, 0.97F, 0.97F), 0.85F);
    private static final int DRAGON_ABILITY_COOLDOWN_TICKS = 20 * 3;
    private static final int WITHER_ABILITY_COOLDOWN_TICKS = 10;
    private static final double SPIDER_CLIMB_SPEED = 0.2D;

    private MilkWorldSystems() {
    }

    public static boolean isAnyMilkBucket(ItemStack stack) {
        for (MilkType type : MilkType.values()) {
            if (type.resolveBucketItem().map(stack::is).orElse(false)) {
                return true;
            }
        }
        return stack.is(net.minecraft.world.item.Items.MILK_BUCKET);
    }

    public static boolean isPlayerMilkBucket(ItemStack stack) {
        return MilkType.PLAYER.resolveBucketItem().map(stack::is).orElse(false);
    }

    public static MilkType findMilkForEntity(Entity entity) {
        Entity milkTarget = resolveMilkTarget(entity);
        if (milkTarget instanceof Chicken) return MilkType.CHICKEN;
        if (milkTarget instanceof Creeper) return MilkType.CREEPER;
        if (milkTarget instanceof EnderMan) return MilkType.ENDERMAN;
        if (milkTarget instanceof Villager) return MilkType.VILLAGER;
        if (milkTarget instanceof Rabbit) return MilkType.RABBIT;
        if (milkTarget instanceof Shulker) return MilkType.SHULKER;
        if (milkTarget instanceof Spider) return MilkType.SPIDER;
        if (milkTarget instanceof EnderDragon) return MilkType.DRAGON;
        if (milkTarget instanceof WitherBoss) return MilkType.WITHER;
        return null;
    }

    public static Entity resolveMilkTarget(Entity entity) {
        if (entity instanceof EnderDragonPart part && part.parentMob != null) {
            return part.parentMob;
        }
        return entity;
    }

    public static boolean canProduceMilk(Entity entity) {
        Entity milkTarget = resolveMilkTarget(entity);
        if (milkTarget instanceof AgeableMob ageableMob) {
            return !ageableMob.isBaby();
        }
        return milkTarget instanceof LivingEntity;
    }

    public static void applyFluidEffects(ServerLevel level, LivingEntity entity, Fluid fluid) {
        for (MilkType type : MilkType.values()) {
            if (ModFluids.SOURCE_FLUIDS.get(type).get() == fluid || ModFluids.FLOWING_FLUIDS.get(type).get() == fluid) {
                if (type != MilkType.CREEPER) {
                    MilkType.playInstantEffect(level, entity, type);
                }
                type.applyStandardEffect(entity, Math.max(type.fluidEffectDurationTicks(), 60));
                if (type.needsActiveAbility()) {
                    MilkAbilityManager.grantAbility(entity, type);
                }
            }
        }
    }

    public static BlockPos findNearbyMilkFluid(Level level, BlockPos center, int radius) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -radius; z <= radius; z++) {
                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    Fluid fluid = level.getFluidState(mutable).getType();
                    if (isMilkFluid(fluid)) {
                        return mutable.immutable();
                    }
                }
            }
        }
        return null;
    }

    public static boolean isMilkFluid(Fluid fluid) {
        return findMilkTypeForFluid(fluid) != null;
    }

    public static MilkType findMilkTypeForFluid(Fluid fluid) {
        for (MilkType type : MilkType.values()) {
            if (ModFluids.SOURCE_FLUIDS.get(type).get() == fluid || ModFluids.FLOWING_FLUIDS.get(type).get() == fluid) {
                return type;
            }
        }
        return null;
    }

    public static void syncActiveAbilityFromFluid(Player player) {
        if (player.tickCount % 10 != 0) {
            return;
        }
        Fluid fluid = player.level().getFluidState(player.blockPosition()).getType();
        MilkType type = findMilkTypeForFluid(fluid);
        if (type != null && type.needsActiveAbility()) {
            MilkAbilityManager.grantAbility(player, type);
        }
    }

    public static void applyMilkRainEffects(ServerLevel level, Set<MilkType> types) {
        if (level.getGameTime() % 2L == 0L) {
            spawnMilkRainVisuals(level);
        }
        for (Player player : level.players()) {
            if (!level.canSeeSky(player.blockPosition())) {
                continue;
            }
            for (MilkType type : types) {
                if (type == MilkType.ENDERMAN) {
                    long cdUntil = player.getPersistentData().getLong("ytls_rain_enderman_cd");
                    if (level.getGameTime() >= cdUntil) {
                        MilkType.playInstantEffect(level, player, type);
                        player.getPersistentData().putLong("ytls_rain_enderman_cd", level.getGameTime() + 20L);
                    }
                } else if (type == MilkType.CREEPER) {
                    if (level.random.nextBoolean()) {
                        spawnRandomExplosion(level);
                    }
                } else {
                    type.applyStandardEffect(player, Math.max(80, type.drinkDurationTicks()));
                    if (type.needsActiveAbility()) {
                        MilkAbilityManager.grantAbility(player, type);
                    }
                }
            }
        }

        if (level.getGameTime() % 80L == 0L) {
            for (Player player : level.players()) {
                spawnHomelanderNear(level, player.blockPosition(), 1 + level.random.nextInt(2));
            }
        }
    }

    public static void spawnRandomExplosion(ServerLevel level) {
        if (level.players().isEmpty()) {
            return;
        }
        Player randomPlayer = level.players().get(level.random.nextInt(level.players().size()));
        double x = randomPlayer.getX() + (level.random.nextDouble() - 0.5D) * 24.0D;
        double y = randomPlayer.getY();
        double z = randomPlayer.getZ() + (level.random.nextDouble() - 0.5D) * 24.0D;
        level.explode(null, x, y, z, 2.2F, Level.ExplosionInteraction.NONE);
    }

    public static void triggerCreeperMilkFluidExplosions(ServerLevel level) {
        if (level.getGameTime() % 20L != 0L) {
            return;
        }
        for (Player player : level.players()) {
            BlockPos pos = findRandomMilkFluid(level, player.blockPosition(), 12, MilkType.CREEPER);
            if (pos != null && level.random.nextBoolean()) {
                level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 2.0F, Level.ExplosionInteraction.NONE);
            }
        }
    }

    private static BlockPos findRandomMilkFluid(Level level, BlockPos center, int radius, MilkType targetType) {
        Fluid targetSource = ModFluids.SOURCE_FLUIDS.get(targetType).get();
        Fluid targetFlowing = ModFluids.FLOWING_FLUIDS.get(targetType).get();
        for (int i = 0; i < 40; i++) {
            BlockPos pos = center.offset(level.random.nextInt(radius * 2 + 1) - radius, level.random.nextInt(5) - 2, level.random.nextInt(radius * 2 + 1) - radius);
            Fluid fluid = level.getFluidState(pos).getType();
            if (fluid == targetSource || fluid == targetFlowing) {
                return pos;
            }
        }
        return null;
    }

    public static void spawnHomelanderNear(ServerLevel level, BlockPos nearPos, int count) {
        for (int i = 0; i < count; i++) {
            BlockPos spawnPos = nearPos.offset(level.random.nextInt(16) - 8, 0, level.random.nextInt(16) - 8);
            HomelanderEntity homelander = ModEntityTypes.HOMELANDER.get().create(level);
            if (homelander != null) {
                homelander.moveTo(spawnPos, level.random.nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(homelander);
            }
        }
    }

    public static void aggroHomelanderForDrinking(Level level, Player player) {
        if (level.isClientSide) {
            return;
        }
        AABB area = player.getBoundingBox().inflate(16.0D);
        for (HomelanderEntity homelander : level.getEntitiesOfClass(HomelanderEntity.class, area)) {
            homelander.setAngryAt(player);
        }
    }

    public static boolean tryUseActiveAbility(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        if (MilkAbilityManager.canUse(player, MilkType.DRAGON)) {
            net.minecraft.world.entity.projectile.DragonFireball fireball = new net.minecraft.world.entity.projectile.DragonFireball(player.level(), player, look.x, look.y, look.z);
            fireball.moveTo(player.getX(), player.getEyeY(), player.getZ());
            player.level().addFreshEntity(fireball);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
            MilkAbilityManager.consumeCooldown(player, MilkType.DRAGON, DRAGON_ABILITY_COOLDOWN_TICKS);
            return true;
        }
        if (MilkAbilityManager.canUse(player, MilkType.WITHER)) {
            net.minecraft.world.entity.projectile.WitherSkull skull = new net.minecraft.world.entity.projectile.WitherSkull(player.level(), player, look.x, look.y, look.z);
            skull.moveTo(player.getX(), player.getEyeY(), player.getZ());
            player.level().addFreshEntity(skull);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
            MilkAbilityManager.consumeCooldown(player, MilkType.WITHER, WITHER_ABILITY_COOLDOWN_TICKS);
            return true;
        }
        return false;
    }

    public static boolean tryLayEgg(ServerPlayer player) {
        if (!MilkAbilityManager.hasAbility(player, MilkType.CHICKEN)) {
            return false;
        }
        Level level = player.level();
        ItemEntity egg = new ItemEntity(level, player.getX(), player.getY() + 0.8D, player.getZ(), new ItemStack(Items.EGG));
        egg.setDefaultPickUpDelay();
        float pitch = player.getXRot();
        float yaw = player.getYRot();
        float sinYaw = Mth.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float cosYaw = Mth.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float sinPitch = -Mth.sin(-pitch * ((float) Math.PI / 180F));
        egg.setDeltaMovement(sinYaw * 0.08D, sinPitch * 0.04D + 0.18D, cosYaw * 0.08D);
        level.addFreshEntity(egg);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHICKEN_EGG, player.getSoundSource(), 1.0F, 1.0F);
        return true;
    }

    public static void applySpiderClimbDuringTravel(Player player) {
        if (!MilkAbilityManager.hasAbility(player, MilkType.SPIDER)) {
            return;
        }
        if (player.getAbilities().flying || player.isPassenger()) {
            return;
        }
        if (player.xxa == 0.0F && player.zza == 0.0F) {
            return;
        }
        if (!isTouchingClimbableWall(player)) {
            return;
        }
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x, SPIDER_CLIMB_SPEED, motion.z);
        player.fallDistance = 0.0F;
        player.setOnGround(false);
    }

    private static boolean isTouchingClimbableWall(Player player) {
        if (player.horizontalCollision) {
            return true;
        }
        Level level = player.level();
        AABB box = player.getBoundingBox();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos base = player.blockPosition();
            for (int y = 0; y < 2; y++) {
                BlockPos wallPos = base.relative(direction).above(y);
                BlockState state = level.getBlockState(wallPos);
                if (state.isAir()) {
                    continue;
                }
                VoxelShape shape = state.getCollisionShape(level, wallPos);
                if (shape.isEmpty()) {
                    continue;
                }
                for (AABB localBox : shape.toAabbs()) {
                    AABB worldBox = localBox.move(wallPos.getX(), wallPos.getY(), wallPos.getZ());
                    if (box.inflate(0.05D, 0.0D, 0.05D).intersects(worldBox) && worldBox.maxY > box.minY + 0.1D) {
                        return true;
                    }
                }
            }
        }
        return isWallInMovementDirection(player);
    }

    private static boolean isWallInMovementDirection(Player player) {
        float forward = player.zza;
        float strafe = player.xxa;
        if (forward == 0.0F && strafe == 0.0F) {
            return false;
        }
        float yawRadians = player.getYRot() * ((float) Math.PI / 180F);
        double moveX = strafe * Math.cos(yawRadians) - forward * Math.sin(yawRadians);
        double moveZ = strafe * Math.sin(yawRadians) + forward * Math.cos(yawRadians);
        if (Math.abs(moveX) < 1.0E-4D && Math.abs(moveZ) < 1.0E-4D) {
            return false;
        }
        Direction direction = Direction.getNearest(moveX, 0.0D, moveZ);
        BlockPos base = player.blockPosition();
        Level level = player.level();
        for (int y = 0; y < 2; y++) {
            BlockPos wallPos = base.relative(direction).above(y);
            if (!level.getBlockState(wallPos).getCollisionShape(level, wallPos).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean tryCollectSelfMilk(ServerPlayer player) {
        if (!player.isShiftKeyDown() || !player.getMainHandItem().is(Items.BUCKET)) {
            return false;
        }
        if (!PlayerLactationManager.consumeForBucket(player)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("泌乳值不足 1000ml"), true);
            return false;
        }
        player.getMainHandItem().shrink(1);
        MilkType.PLAYER.resolveBucketItem().ifPresent(item -> player.addItem(new ItemStack(item)));
        return true;
    }

    public static void spawnMilkOverflowParticles(ServerLevel level, Player player) {
        for (int i = 0; i < 8; i++) {
            double x = player.getX() + (level.random.nextDouble() - 0.5D) * 0.7D;
            double y = player.getY() + 1.2D + level.random.nextDouble() * 0.8D;
            double z = player.getZ() + (level.random.nextDouble() - 0.5D) * 0.7D;
            level.sendParticles(MILK_DROP_PARTICLE, x, y, z, 1, 0, -0.08D, 0, 0.01D);
        }
    }

    public static void spawnMilkRainVisuals(ServerLevel level) {
        for (Player player : level.players()) {
            BlockPos center = player.blockPosition();
            for (int i = 0; i < 42; i++) {
                int x = center.getX() + level.random.nextInt(21) - 10;
                int z = center.getZ() + level.random.nextInt(21) - 10;
                int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                if (groundY < center.getY() - 12 || groundY > center.getY() + 16) {
                    continue;
                }
                double px = x + level.random.nextDouble();
                double pz = z + level.random.nextDouble();
                double py = Math.max(center.getY() + 3.0D, groundY + 7.0D + level.random.nextDouble() * 7.0D);
                double windX = (level.random.nextDouble() - 0.5D) * 0.025D;
                double windZ = (level.random.nextDouble() - 0.5D) * 0.025D;
                level.sendParticles(ModParticles.MILK_RAIN_STREAK.get(), px, py, pz, 0, windX, -0.34D, windZ, 1.0D);

                if (level.random.nextInt(3) == 0 && level.canSeeSky(new BlockPos(x, groundY, z))) {
                    level.sendParticles(
                        ModParticles.MILK_RAIN_SPLASH.get(),
                        px,
                        groundY + 0.08D,
                        pz,
                        2,
                        0.035D,
                        0.06D,
                        0.035D,
                        0.02D
                    );
                }
            }
        }
    }
}
