package icu.ytlsnb.ytls.block.entityblock.blockentity;

import icu.ytlsnb.ytls.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ObsidianCounterBlockEntity extends BlockEntity {
    private int count = 0;

    public ObsidianCounterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OBSIDIAN_COUNTER.get(), pos, state);
    }

    public void increment() {
        count++;
        setChanged(); // 标记数据已改变
    }

    public int getCount() {
        return count;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("count", this.count);
        System.out.println("save count = " + this.count);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.count = tag.getInt("count");
        System.out.println("load count = " + this.count);
    }
}
