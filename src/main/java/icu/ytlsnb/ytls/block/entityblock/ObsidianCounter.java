package icu.ytlsnb.ytls.block.entityblock;

import com.mojang.serialization.MapCodec;
import icu.ytlsnb.ytls.block.entityblock.blockentity.ObsidianCounterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ObsidianCounter extends BaseEntityBlock {
    public static final @NotNull MapCodec<ObsidianCounter> CODEC = simpleCodec(ObsidianCounter::new);

    public ObsidianCounter(BlockBehaviour.Properties properties) {// 必须用有参构造，否则CODEC创建会报错
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ObsidianCounterBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ObsidianCounterBlockEntity counter) {
                counter.increment();
                player.sendSystemMessage(
                        Component.literal("当前计数: " + counter.getCount())
                );
            }
        }
        return InteractionResult.SUCCESS;
    }
}
