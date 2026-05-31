package icu.ytlsnb.ytls.blockentity;

import icu.ytlsnb.ytls.menu.MilkAltarMenu;
import icu.ytlsnb.ytls.milk.MilkType;
import icu.ytlsnb.ytls.registry.ModBlockEntities;
import icu.ytlsnb.ytls.system.MilkRainManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MilkAltarBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> itemHandler);

    public MilkAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MILK_ALTAR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MilkAltarBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        blockEntity.getCurrentMilkType().ifPresent(type -> MilkRainManager.reportAltarMilk(level, type));
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public java.util.Optional<MilkType> getCurrentMilkType() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) {
            return java.util.Optional.empty();
        }
        for (MilkType value : MilkType.values()) {
            if (value.resolveBucketItem().map(stack::is).orElse(false)) {
                return java.util.Optional.of(value);
            }
        }
        return java.util.Optional.empty();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ytls.milk_altar");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new MilkAltarMenu(containerId, inventory, this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
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
        itemCapability = LazyOptional.of(() -> itemHandler);
    }
}
