package icu.ytlsnb.ytls.gameplay.toilet.blockentity;

import icu.ytlsnb.ytls.gameplay.toilet.block.ToiletBlock;
import icu.ytlsnb.ytls.gameplay.toilet.entity.ToiletSeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ToiletBlockEntity extends BlockEntity {
    private static final int POOP_INTERVAL_TICKS = 20 * 60;
    private int sitTicks;

    public ToiletBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ToiletBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        ToiletSeatEntity seat = ToiletSeatEntity.findSeatAt(level, pos);
        boolean playerSitting = seat != null && seat.getPassengers().stream().anyMatch(p -> p instanceof Player);
        if (!playerSitting) {
            entity.sitTicks = 0;
            return;
        }
        entity.sitTicks++;
        if (entity.sitTicks < POOP_INTERVAL_TICKS) {
            return;
        }
        entity.sitTicks = 0;
        int current = ToiletBlock.getPoopCount(state);
        if (current < 3) {
            ToiletBlock.setPoopCount(level, pos, current + 1);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("SitTicks", sitTicks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        sitTicks = tag.getInt("SitTicks");
    }
}
