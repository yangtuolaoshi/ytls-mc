package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class PlungerCropHandler {
    private PlungerCropHandler() {
    }

    public static boolean trySuckCrop(Level level, BlockPos pos, BlockState state, Player player, int fortune) {
        Block block = state.getBlock();
        if (!(block instanceof CropBlock) && !(block instanceof StemBlock)) {
            return false;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        double growChance = 0.30D + fortune * 0.05D;
        if (serverLevel.random.nextDouble() < growChance) {
            if (block instanceof CropBlock crop) {
                if (!crop.isMaxAge(state)) {
                    serverLevel.setBlock(pos, crop.getStateForAge(crop.getAge(state) + 1), Block.UPDATE_ALL);
                } else {
                    crop.performBonemeal(serverLevel, serverLevel.random, pos, state);
                }
            } else if (block instanceof StemBlock stem) {
                stem.performBonemeal(serverLevel, serverLevel.random, pos, state);
            }
            return true;
        }
        Block.getDrops(state, serverLevel, pos, serverLevel.getBlockEntity(pos), player, player.getMainHandItem())
                .forEach(stack -> PlungerBlockHandler.spawnDrops(serverLevel, pos, stack, fortune));
        serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        return true;
    }
}
