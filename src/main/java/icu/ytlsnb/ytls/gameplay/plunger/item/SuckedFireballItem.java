package icu.ytlsnb.ytls.gameplay.plunger.item;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 吸火焰得到的火球：右键喷火，点燃视线前方方块与生物。
 */
@RegisterItem("sucked_fireball")
public class SuckedFireballItem extends Item {
    public static final int MAX_DURABILITY = 4;
    private static final double RANGE = 8.0D;

    public SuckedFireballItem() {
        super(new Item.Properties().durability(MAX_DURABILITY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(RANGE));

        spawnSprayParticles((ServerLevel) level, eye, look);

        BlockHitResult blockHit = level.clip(new ClipContext(
                eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        BlockPos firePos;
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            firePos = blockHit.getBlockPos().relative(blockHit.getDirection());
        } else {
            firePos = BlockPos.containing(end);
        }

        if (BaseFireBlock.canBePlacedAt(level, firePos, Direction.DOWN)
                || level.getBlockState(firePos).canBeReplaced()) {
            if (level.getBlockState(firePos).isAir() || level.getBlockState(firePos).canBeReplaced()) {
                level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 11);
            }
        }

        AABB box = new AABB(eye, end).inflate(0.75D);
        for (Entity entity : level.getEntities(player, box, e -> e instanceof LivingEntity && e.isAlive())) {
            if (entity instanceof LivingEntity living) {
                living.setSecondsOnFire(6);
            }
        }

        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
        player.getCooldowns().addCooldown(this, 8);
        return InteractionResultHolder.consume(stack);
    }

    private static void spawnSprayParticles(ServerLevel level, Vec3 eye, Vec3 look) {
        for (int i = 1; i <= 16; i++) {
            Vec3 pos = eye.add(look.scale(i * 0.45D));
            double spread = 0.08D + i * 0.01D;
            level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 3, spread, spread, spread, 0.01D);
            if (i % 2 == 0) {
                level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 1, spread, spread, spread, 0.02D);
            }
        }
    }
}
