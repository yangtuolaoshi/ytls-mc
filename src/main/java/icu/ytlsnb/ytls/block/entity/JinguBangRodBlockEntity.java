package icu.ytlsnb.ytls.block.entity;

import icu.ytlsnb.ytls.block.JinguBangRodBlock;
import icu.ytlsnb.ytls.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class JinguBangRodBlockEntity extends BlockEntity {
    private int extendGrid;

    public JinguBangRodBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.JINGU_BANG_ROD.get(), pos, state);
    }

    public int getExtendGrid() {
        return extendGrid;
    }

    public void setExtendGrid(int extendGrid) {
        this.extendGrid = Math.max(0, extendGrid);
        setChanged();
        if (level != null) {
            BlockState st = getBlockState();
            level.sendBlockUpdated(worldPosition, st, st, 3);
        }
    }

    /**
     * 伸长一格：清除伸长方向上的阻挡方块并更新碰撞体积。
     */
    public void tryExtendAlong(Level level, BlockPos base, Direction facing) {
        extendGrid++;
        BlockPos target = base.relative(facing, extendGrid);
        if (!level.isEmptyBlock(target) && !level.getBlockState(target).isAir()) {
            level.destroyBlock(target, true);
        }
        setChanged();
        BlockState st = getBlockState();
        level.sendBlockUpdated(base, st, st, 3);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Extend", extendGrid);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        extendGrid = tag.getInt("Extend");
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("Extend", extendGrid);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag) {
        super.handleUpdateTag(tag);
        extendGrid = tag.getInt("Extend");
    }
}
