package icu.ytlsnb.ytls.menu;

import icu.ytlsnb.ytls.blockentity.MilkAltarBlockEntity;
import icu.ytlsnb.ytls.registry.ModMenuTypes;
import icu.ytlsnb.ytls.system.MilkWorldSystems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class MilkAltarMenu extends AbstractContainerMenu {

    private final MilkAltarBlockEntity blockEntity;
    private final Level level;

    public MilkAltarMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, (MilkAltarBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public MilkAltarMenu(int id, Inventory inventory, MilkAltarBlockEntity blockEntity) {
        super(ModMenuTypes.MILK_ALTAR.get(), id);
        this.blockEntity = blockEntity;
        this.level = inventory.player.level();

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 80, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return MilkWorldSystems.isAnyMilkBucket(stack);
                }
            });
        });

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 109));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return !blockEntity.isRemoved() && player.distanceToSqr(
            blockEntity.getBlockPos().getX() + 0.5D,
            blockEntity.getBlockPos().getY() + 0.5D,
            blockEntity.getBlockPos().getZ() + 0.5D
        ) <= 64.0D;
    }
}
