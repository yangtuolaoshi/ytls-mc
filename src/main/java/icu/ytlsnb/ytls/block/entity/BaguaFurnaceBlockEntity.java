package icu.ytlsnb.ytls.block.entity;

import icu.ytlsnb.ytls.block.BaguaFurnaceBlock;
import icu.ytlsnb.ytls.init.ModBlockEntities;
import icu.ytlsnb.ytls.recipe.BaguaSmeltOutcome;
import icu.ytlsnb.ytls.world.inventory.BaguaFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaguaFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_FUEL = 0;
    public static final int SLOT_MATERIAL_START = 1;
    public static final int SLOT_MATERIAL_COUNT = 3;
    public static final int SLOT_OUTPUT = 4;
    public static final int CONTAINER_SIZE = 5;

    private final ItemStackHandler inventory = new ItemStackHandler(CONTAINER_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            BaguaFurnaceBlockEntity.this.setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == SLOT_OUTPUT) {
                return false;
            }
            if (slot == SLOT_FUEL) {
                return ForgeHooks.getBurnTime(stack, net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0;
            }
            return slot >= SLOT_MATERIAL_START && slot < SLOT_MATERIAL_START + SLOT_MATERIAL_COUNT;
        }
    };

    private LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> inventory);

    private int litTime;
    private int litDuration;
    private int cookingProgress;
    private static final int COOK_DURATION = 200;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BaguaFurnaceBlockEntity.this.litTime;
                case 1 -> BaguaFurnaceBlockEntity.this.litDuration;
                case 2 -> BaguaFurnaceBlockEntity.this.cookingProgress;
                case 3 -> COOK_DURATION;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> BaguaFurnaceBlockEntity.this.litTime = value;
                case 1 -> BaguaFurnaceBlockEntity.this.litDuration = value;
                case 2 -> BaguaFurnaceBlockEntity.this.cookingProgress = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public BaguaFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BAGUA_FURNACE.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5
        ) <= 64.0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BaguaFurnaceBlockEntity be) {
        boolean wasBurning = be.isBurning();
        boolean dirty = false;
        if (be.isBurning()) {
            be.litTime--;
        }

        if (!level.isClientSide) {
            ItemStack fuel = be.inventory.getStackInSlot(SLOT_FUEL);
            boolean canWork = be.canSmelt();

            if (!be.isBurning() && canWork && !fuel.isEmpty()) {
                int burnTicks = ForgeHooks.getBurnTime(fuel, net.minecraft.world.item.crafting.RecipeType.SMELTING);
                if (burnTicks > 0) {
                    be.litTime = burnTicks;
                    be.litDuration = burnTicks;
                    if (fuel.hasCraftingRemainingItem()) {
                        be.inventory.setStackInSlot(SLOT_FUEL, fuel.getCraftingRemainingItem());
                    } else {
                        fuel.shrink(1);
                        be.inventory.setStackInSlot(SLOT_FUEL, fuel.isEmpty() ? ItemStack.EMPTY : fuel);
                    }
                    dirty = true;
                }
            }

            if (be.isBurning() && canWork) {
                be.cookingProgress++;
                if (be.cookingProgress >= COOK_DURATION) {
                    be.finishSmelt(level.random);
                    be.cookingProgress = 0;
                    dirty = true;
                }
            } else {
                be.cookingProgress = 0;
            }

            if (wasBurning != be.isBurning()) {
                dirty = true;
                level.setBlock(pos, state.setValue(BaguaFurnaceBlock.LIT, be.isBurning()), 3);
            }

            if (dirty) {
                be.setChanged();
            }
        }
    }

    private boolean canSmelt() {
        BaguaSmeltOutcome outcome = BaguaSmeltOutcome.resolve(
                inventory.getStackInSlot(SLOT_MATERIAL_START),
                inventory.getStackInSlot(SLOT_MATERIAL_START + 1),
                inventory.getStackInSlot(SLOT_MATERIAL_START + 2)
        );
        if (outcome == null) {
            return false;
        }
        return outcome.canAcceptCurrentOutput(inventory.getStackInSlot(SLOT_OUTPUT));
    }

    private void finishSmelt(net.minecraft.util.RandomSource random) {
        BaguaSmeltOutcome outcome = BaguaSmeltOutcome.resolve(
                inventory.getStackInSlot(SLOT_MATERIAL_START),
                inventory.getStackInSlot(SLOT_MATERIAL_START + 1),
                inventory.getStackInSlot(SLOT_MATERIAL_START + 2)
        );
        if (outcome == null || !outcome.canAcceptCurrentOutput(inventory.getStackInSlot(SLOT_OUTPUT))) {
            return;
        }
        ItemStack produced = outcome.produce(random);
        ItemStack out = inventory.getStackInSlot(SLOT_OUTPUT);
        if (out.isEmpty()) {
            inventory.setStackInSlot(SLOT_OUTPUT, produced.copyWithCount(1));
        } else {
            out.grow(1);
            inventory.setStackInSlot(SLOT_OUTPUT, out);
        }

        for (int i = 0; i < SLOT_MATERIAL_COUNT; i++) {
            int slot = SLOT_MATERIAL_START + i;
            ItemStack s = inventory.getStackInSlot(slot);
            if (!s.isEmpty()) {
                s.shrink(1);
                inventory.setStackInSlot(slot, s.isEmpty() ? ItemStack.EMPTY : s);
            }
        }
    }

    private boolean isBurning() {
        return litTime > 0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.ytls.bagua_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, @NotNull Inventory playerInv, @NotNull Player player) {
        return new BaguaFurnaceMenu(syncId, playerInv, this, dataAccess);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.putInt("LitTime", litTime);
        tag.putInt("LitDuration", litDuration);
        tag.putInt("CookingProgress", cookingProgress);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        litTime = tag.getInt("LitTime");
        litDuration = tag.getInt("LitDuration");
        cookingProgress = tag.getInt("CookingProgress");
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (!remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemCapability = LazyOptional.of(() -> inventory);
    }

    public void dropInventoryContents() {
        if (level == null) {
            return;
        }
        net.minecraft.world.SimpleContainer tmp = new net.minecraft.world.SimpleContainer(CONTAINER_SIZE);
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            tmp.setItem(i, inventory.getStackInSlot(i).copy());
        }
        net.minecraft.world.Containers.dropContents(level, worldPosition, tmp);
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}
