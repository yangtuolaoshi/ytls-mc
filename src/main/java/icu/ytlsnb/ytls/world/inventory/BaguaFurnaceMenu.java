package icu.ytlsnb.ytls.world.inventory;

import icu.ytlsnb.ytls.block.entity.BaguaFurnaceBlockEntity;
import icu.ytlsnb.ytls.init.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class BaguaFurnaceMenu extends AbstractContainerMenu {
    private final BaguaFurnaceBlockEntity blockEntity;
    private final ContainerData data;

    public BaguaFurnaceMenu(int syncId, Inventory playerInv, BaguaFurnaceBlockEntity be, ContainerData data) {
        super(ModMenus.BAGUA_FURNACE.get(), syncId);
        this.blockEntity = be;
        this.data = data;
        checkContainerDataCount(data, 4);

        addSlot(new SlotItemHandler(be.getInventory(), BaguaFurnaceBlockEntity.SLOT_FUEL, 56, 53));

        addSlot(new SlotItemHandler(be.getInventory(), BaguaFurnaceBlockEntity.SLOT_MATERIAL_START, 44, 17));
        addSlot(new SlotItemHandler(be.getInventory(), BaguaFurnaceBlockEntity.SLOT_MATERIAL_START + 1, 62, 17));
        addSlot(new SlotItemHandler(be.getInventory(), BaguaFurnaceBlockEntity.SLOT_MATERIAL_START + 2, 80, 17));

        addSlot(new SlotItemHandler(be.getInventory(), BaguaFurnaceBlockEntity.SLOT_OUTPUT, 116, 35) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    public static BaguaFurnaceMenu fromNetwork(int syncId, Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BaguaFurnaceBlockEntity be = (BaguaFurnaceBlockEntity) inv.player.level().getBlockEntity(pos);
        ContainerData data = be != null ? be.getDataAccess() : new SimpleContainerData(4);
        return new BaguaFurnaceMenu(syncId, inv, be, data);
    }

    public int getLitProgress() {
        int litTime = data.get(0);
        int litDuration = data.get(1);
        return litDuration == 0 ? 0 : litTime * 13 / litDuration;
    }

    public int getCookProgress() {
        int progress = data.get(2);
        int total = data.get(3);
        return total == 0 ? 0 : progress * 24 / total;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            int containerSlots = BaguaFurnaceBlockEntity.CONTAINER_SIZE;
            if (index < containerSlots) {
                if (!moveItemStackTo(stack, containerSlots, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, BaguaFurnaceBlockEntity.SLOT_FUEL, BaguaFurnaceBlockEntity.SLOT_FUEL + 1, false)) {
                if (!moveItemStackTo(stack, BaguaFurnaceBlockEntity.SLOT_MATERIAL_START,
                        BaguaFurnaceBlockEntity.SLOT_MATERIAL_START + BaguaFurnaceBlockEntity.SLOT_MATERIAL_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
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
        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return blockEntity != null && blockEntity.stillValid(player);
    }
}
