package icu.ytlsnb.ytls.gameplay.plunger.item;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterItem;
import icu.ytlsnb.ytls.gameplay.plunger.handler.PlungerBlockHandler;
import icu.ytlsnb.ytls.gameplay.plunger.handler.PlungerEntityHandler;
import icu.ytlsnb.ytls.gameplay.plunger.handler.PlungerModeHelper;
import icu.ytlsnb.ytls.gameplay.plunger.handler.PlungerSoundHandler;
import icu.ytlsnb.ytls.gameplay.plunger.handler.PlungerSuctionHandler;
import icu.ytlsnb.ytls.gameplay.plunger.handler.PlungerWeatherHandler;
import icu.ytlsnb.ytls.gameplay.toilet.block.ToiletBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@RegisterItem("plunger")
public class PlungerItem extends Item {
    public static final int MAX_DURABILITY = 50;

    public PlungerItem() {
        super(new Item.Properties().durability(MAX_DURABILITY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        // 吸附中：任意右键发射
        if (PlungerSuctionHandler.hasSuckedEntity(stack)) {
            return PlungerSuctionHandler.launchSucked(player, stack)
                    ? InteractionResult.CONSUME
                    : InteractionResult.FAIL;
        }
        // 吸声音模式不处理方块
        if (PlungerModeHelper.isSoundMode(stack)) {
            return InteractionResult.PASS;
        }
        // Shift 时优先处理实体吸附，不对方块生效
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof ToiletBlock) {
            if (PlungerBlockHandler.suctionToilet(level, pos, state, player, stack)) {
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        if (PlungerBlockHandler.handleBlock(level, pos, state, player, stack)) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    /**
     * 实体交互主路径在 {@link icu.ytlsnb.ytls.gameplay.plunger.handler.PlungerInteractionHandler}，
     * 此处保留为兜底（事件未拦截时仍可生效）。
     */
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        // 吸附中：任意右键发射，不再造成伤害
        if (PlungerSuctionHandler.hasSuckedEntity(stack)) {
            return PlungerSuctionHandler.launchSucked(player, stack)
                    ? InteractionResult.CONSUME
                    : InteractionResult.FAIL;
        }
        if (PlungerModeHelper.isSoundMode(stack)) {
            return PlungerSoundHandler.trySuckSounds(player, stack, entity)
                    ? InteractionResult.CONSUME
                    : InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            return PlungerSuctionHandler.tryCapture(player, stack, entity)
                    ? InteractionResult.CONSUME
                    : InteractionResult.FAIL;
        }
        return PlungerEntityHandler.handle(player, stack, entity)
                ? InteractionResult.CONSUME
                : InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        // 吸附中：任意右键（对空）发射
        if (PlungerSuctionHandler.hasSuckedEntity(stack)) {
            if (PlungerSuctionHandler.launchSucked(player, stack)) {
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.fail(stack);
        }

        // 吸声音模式：对空播放已存声音
        if (PlungerModeHelper.isSoundMode(stack)) {
            LivingEntity lookTarget = findLookTarget(player, 5.0D);
            if (lookTarget != null && PlungerSoundHandler.trySuckSounds(player, stack, lookTarget)) {
                return InteractionResultHolder.consume(stack);
            }
            if (PlungerSoundHandler.tryPlayStoredSound(level, player, stack)) {
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.pass(stack);
        }

        // 仰头 ≥60°：吸天气
        if (!player.isShiftKeyDown() && PlungerWeatherHandler.trySuckWeather(level, player, stack)) {
            return InteractionResultHolder.consume(stack);
        }

        if (!player.isShiftKeyDown()) {
            LivingEntity target = findLookTarget(player, 5.0D);
            if (target != null && PlungerEntityHandler.handle(player, stack, target)) {
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.pass(stack);
        }
        LivingEntity target = findLookTarget(player, 5.0D);
        if (target != null && PlungerSuctionHandler.tryCapture(player, stack, target)) {
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Nullable
    private static LivingEntity findLookTarget(Player player, double range) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(range));
        AABB box = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0D);
        LivingEntity closest = null;
        double closestDist = range;
        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e != player && e.isAlive() && e.isPickable())) {
            AABB entityBox = entity.getBoundingBox().inflate(0.3D);
            var optional = entityBox.clip(eye, end);
            if (optional.isPresent()) {
                double dist = eye.distanceTo(optional.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                }
            }
        }
        return closest;
    }
}
