package icu.ytlsnb.ytls.world.inventory;

import icu.ytlsnb.ytls.init.ModMenus;
import icu.ytlsnb.ytls.item.MiniBaguaFurnaceItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.NotNull;

public class MiniBaguaMenu extends AbstractContainerMenu {
    private final Player player;
    private final int bagSlotIndex;
    private final SimpleContainer fuels = new SimpleContainer(4) {
        @Override
        public void setChanged() {
            super.setChanged();
            MiniBaguaMenu.this.pushToItemStack();
        }
    };

    public MiniBaguaMenu(int syncId, Inventory inv, int bagSlotIndex) {
        super(ModMenus.MINI_BAGUA.get(), syncId);
        this.player = inv.player;
        this.bagSlotIndex = bagSlotIndex;
        pullFromItemStack();

        addSlot(new MushroomSlot(fuels, 0, 44, 35, Items.RED_MUSHROOM));
        addSlot(new MushroomSlot(fuels, 1, 62, 35, Items.BROWN_MUSHROOM));
        addSlot(new MushroomSlot(fuels, 2, 80, 35, Items.WARPED_FUNGUS));
        addSlot(new MushroomSlot(fuels, 3, 98, 35, Items.CRIMSON_FUNGUS));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    public static MiniBaguaMenu fromNetwork(int syncId, Inventory inv, FriendlyByteBuf buf) {
        return new MiniBaguaMenu(syncId, inv, buf.readVarInt());
    }

    private void pullFromItemStack() {
        ItemStack bag = player.getInventory().getItem(bagSlotIndex);
        fuels.setItem(0, stackOf(Items.RED_MUSHROOM, MiniBaguaFurnaceItem.getFuel(bag, MiniBaguaFurnaceItem.TAG_RED)));
        fuels.setItem(1, stackOf(Items.BROWN_MUSHROOM, MiniBaguaFurnaceItem.getFuel(bag, MiniBaguaFurnaceItem.TAG_BROWN)));
        fuels.setItem(2, stackOf(Items.WARPED_FUNGUS, MiniBaguaFurnaceItem.getFuel(bag, MiniBaguaFurnaceItem.TAG_WARPED)));
        fuels.setItem(3, stackOf(Items.CRIMSON_FUNGUS, MiniBaguaFurnaceItem.getFuel(bag, MiniBaguaFurnaceItem.TAG_CRIMSON)));
    }

    private static ItemStack stackOf(net.minecraft.world.item.Item item, int count) {
        return count <= 0 ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    private void pushToItemStack() {
        ItemStack bag = player.getInventory().getItem(bagSlotIndex);
        if (!(bag.getItem() instanceof MiniBaguaFurnaceItem)) {
            return;
        }
        MiniBaguaFurnaceItem.setFuel(bag, MiniBaguaFurnaceItem.TAG_RED, countFor(fuels.getItem(0), Items.RED_MUSHROOM));
        MiniBaguaFurnaceItem.setFuel(bag, MiniBaguaFurnaceItem.TAG_BROWN, countFor(fuels.getItem(1), Items.BROWN_MUSHROOM));
        MiniBaguaFurnaceItem.setFuel(bag, MiniBaguaFurnaceItem.TAG_WARPED, countFor(fuels.getItem(2), Items.WARPED_FUNGUS));
        MiniBaguaFurnaceItem.setFuel(bag, MiniBaguaFurnaceItem.TAG_CRIMSON, countFor(fuels.getItem(3), Items.CRIMSON_FUNGUS));
    }

    private static int countFor(ItemStack stack, net.minecraft.world.item.Item allowed) {
        return stack.is(allowed) ? stack.getCount() : 0;
    }

    @Override
    public void clicked(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
        super.clicked(slotId, dragType, clickType, player);
        pushToItemStack();
    }

    @Override
    public void removed(@NotNull Player player) {
        pushToItemStack();
        super.removed(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index < 4) {
                if (!moveItemStackTo(stack, 4, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, 4, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        pushToItemStack();
        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        ItemStack bag = player.getInventory().getItem(bagSlotIndex);
        return bag.getItem() instanceof MiniBaguaFurnaceItem;
    }

    static final class MushroomSlot extends Slot {
        private final net.minecraft.world.item.Item allowed;

        MushroomSlot(SimpleContainer container, int slot, int x, int y, net.minecraft.world.item.Item allowed) {
            super(container, slot, x, y);
            this.allowed = allowed;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.is(allowed);
        }
    }
}
