package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class PlungerContainerHandler {
    private PlungerContainerHandler() {
    }

    public static boolean trySuckContainer(Level level, BlockPos pos, BlockState state, Player player, int fortune) {
        Block block = state.getBlock();
        if (!(block instanceof ChestBlock
                || block instanceof AbstractFurnaceBlock
                || block instanceof BarrelBlock
                || block instanceof DispenserBlock
                || block instanceof DropperBlock)) {
            return false;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof Container container)) {
            return false;
        }
        // 未打开过的战利品箱需要先展开战利品表
        if (be instanceof RandomizableContainerBlockEntity randomizable) {
            randomizable.unpackLootTable(player);
        }
        int slot = pickNonEmptySlot(container, level);
        if (slot < 0) {
            return false;
        }
        ItemStack extracted = extractOne(container, slot);
        if (extracted.isEmpty()) {
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            PlungerBlockHandler.spawnFortuneCopies(serverLevel, pos.above(), extracted, fortune);
        }
        return true;
    }

    private static int pickNonEmptySlot(Container container, Level level) {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (!container.getItem(i).isEmpty()) {
                slots.add(i);
            }
        }
        if (slots.isEmpty()) {
            return -1;
        }
        return slots.get(level.random.nextInt(slots.size()));
    }

    private static ItemStack extractOne(Container container, int slot) {
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
