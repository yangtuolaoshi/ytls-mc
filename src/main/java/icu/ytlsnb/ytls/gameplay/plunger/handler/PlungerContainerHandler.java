package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class PlungerContainerHandler {
    private PlungerContainerHandler() {
    }

    public static boolean trySuckContainer(Level level, BlockPos pos, BlockState state, Player player, int fortune) {
        Block block = state.getBlock();
        if (!(block instanceof ChestBlock || block instanceof FurnaceBlock || block instanceof BarrelBlock
                || block instanceof DispenserBlock || block instanceof DropperBlock)) {
            return false;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            return false;
        }
        int slot = pickNonEmptySlot(be);
        if (slot < 0) {
            return false;
        }
        ItemStack extracted = extractOne(be, slot);
        if (extracted.isEmpty()) {
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            PlungerBlockHandler.spawnFortuneCopies(serverLevel, pos.above(), extracted, fortune);
        }
        return true;
    }

    private static int pickNonEmptySlot(BlockEntity be) {
        List<Integer> slots = new ArrayList<>();
        if (be instanceof RandomizableContainerBlockEntity container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                if (!container.getItem(i).isEmpty()) {
                    slots.add(i);
                }
            }
        } else if (be instanceof FurnaceBlockEntity furnace) {
            for (int i = 0; i < 3; i++) {
                if (!furnace.getItem(i).isEmpty()) {
                    slots.add(i);
                }
            }
        }
        if (slots.isEmpty()) {
            return -1;
        }
        return slots.get(be.getLevel() != null
                ? be.getLevel().random.nextInt(slots.size())
                : 0);
    }

    private static ItemStack extractOne(BlockEntity be, int slot) {
        Container container;
        if (be instanceof RandomizableContainerBlockEntity randomizable) {
            container = randomizable;
        } else if (be instanceof FurnaceBlockEntity furnace) {
            container = furnace;
        } else {
            return ItemStack.EMPTY;
        }
        ItemStack stack = container.getItem(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack one = stack.copyWithCount(1);
        stack.shrink(1);
        container.setChanged();
        return one;
    }
}
