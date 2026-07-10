package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
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
        List<ItemStack> candidates = collectItems(be);
        if (candidates.isEmpty()) {
            return false;
        }
        ItemStack picked = candidates.get(level.random.nextInt(candidates.size()));
        removeOne(be, picked);
        if (level instanceof ServerLevel serverLevel) {
            PlungerBlockHandler.spawnDrops(serverLevel, pos.above(), picked, fortune);
        }
        return true;
    }

    private static List<ItemStack> collectItems(BlockEntity be) {
        List<ItemStack> items = new ArrayList<>();
        if (be instanceof RandomizableContainerBlockEntity container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.isEmpty()) {
                    items.add(stack);
                }
            }
        } else if (be instanceof FurnaceBlockEntity furnace) {
            addIfPresent(items, furnace.getItem(0));
            addIfPresent(items, furnace.getItem(1));
            addIfPresent(items, furnace.getItem(2));
        }
        return items;
    }

    private static void addIfPresent(List<ItemStack> items, ItemStack stack) {
        if (!stack.isEmpty()) {
            items.add(stack);
        }
    }

    private static void removeOne(BlockEntity be, ItemStack template) {
        if (be instanceof RandomizableContainerBlockEntity container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (ItemStack.isSameItemSameTags(stack, template)) {
                    stack.shrink(1);
                    container.setChanged();
                    return;
                }
            }
        } else if (be instanceof FurnaceBlockEntity furnace) {
            shrinkMatching(furnace, template, 0);
            shrinkMatching(furnace, template, 1);
            shrinkMatching(furnace, template, 2);
        }
    }

    private static void shrinkMatching(Container container, ItemStack template, int slot) {
        ItemStack stack = container.getItem(slot);
        if (ItemStack.isSameItemSameTags(stack, template)) {
            stack.shrink(1);
            container.setChanged();
        }
    }
}
