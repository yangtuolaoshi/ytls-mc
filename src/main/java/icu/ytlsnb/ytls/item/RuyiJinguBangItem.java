package icu.ytlsnb.ytls.item;

import icu.ytlsnb.ytls.block.JinguBangRodBlock;
import icu.ytlsnb.ytls.block.entity.JinguBangRodBlockEntity;
import icu.ytlsnb.ytls.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

/**
 * 手持伸长使用右键；Shift+右键参照末地烛方式放置；伸长格数存于 NBT。
 */
public class RuyiJinguBangItem extends SwordItem {
    private static final String TAG_EXTEND = "YtlsExtendGrid";

    public RuyiJinguBangItem() {
        super(ModItemTier.JINGU_BANG, 3, -2.4F, new Properties().fireResistant());
    }

    public static int getExtend(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(TAG_EXTEND) : 0;
    }

    public static void setExtend(ItemStack stack, int grid) {
        stack.getOrCreateTag().putInt(TAG_EXTEND, Math.max(0, grid));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }
        if (!level.isClientSide) {
            setExtend(stack, getExtend(stack) + 1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        Direction dir = context.getClickedFace();
        BlockPos clicked = context.getClickedPos();
        BlockPos place = clicked.relative(dir);
        BlockState state = ModBlocks.PLACED_JINGU_BANG.get().defaultBlockState().setValue(JinguBangRodBlock.FACING, dir);
        BlockHitResult hit = new BlockHitResult(
                context.getClickLocation(),
                context.getClickedFace(),
                context.getClickedPos(),
                context.isInside()
        );
        BlockPlaceContext placeContext = new BlockPlaceContext(level, player, context.getHand(), context.getItemInHand(), hit);
        if (!level.getBlockState(place).canBeReplaced(placeContext)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide) {
            level.setBlock(place, state, 3);
            if (level.getBlockEntity(place) instanceof JinguBangRodBlockEntity rod) {
                rod.setExtendGrid(getExtend(context.getItemInHand()));
            }
            if (!player.getAbilities().instabuild) {
                context.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
