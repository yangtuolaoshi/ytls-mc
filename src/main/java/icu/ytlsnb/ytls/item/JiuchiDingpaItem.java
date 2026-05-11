package icu.ytlsnb.ytls.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 九齿钉耙：对点击地块为中心 3×3 尝试锄地（泥土类且上方空气）。
 */
public class JiuchiDingpaItem extends HoeItem {
    public JiuchiDingpaItem() {
        super(ModItemTier.JIUCHI_RAKE, -2, -1.0F, new Properties().fireResistant());
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        boolean any = false;
        BlockPos center = context.getClickedPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (tryTill(level, center.offset(dx, 0, dz))) {
                    any = true;
                }
            }
        }
        if (any) {
            context.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
            level.playSound(player, center, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private boolean tryTill(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());
        if (!above.isAir()) {
            return false;
        }
        if (!state.is(BlockTags.DIRT)) {
            return false;
        }
        level.setBlock(pos, Blocks.FARMLAND.defaultBlockState(), 11);
        return true;
    }
}
