package icu.ytlsnb.ytls.block;

import com.mojang.serialization.MapCodec;
import icu.ytlsnb.ytls.block.entity.JinguBangRodBlockEntity;
import icu.ytlsnb.ytls.init.ModItems;
import icu.ytlsnb.ytls.item.RuyiJinguBangItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JinguBangRodBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final MapCodec<JinguBangRodBlock> CODEC = simpleCodec(JinguBangRodBlock::new);

    public JinguBangRodBlock() {
        this(createProperties());
    }

    private JinguBangRodBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    private static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.GOLD)
                .noOcclusion()
                .strength(5.0F, 1200.0F)
                .sound(SoundType.NETHERITE_BLOCK)
                .requiresCorrectToolForDrops()
                .pushReaction(PushReaction.DESTROY);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new JinguBangRodBlockEntity(pos, state);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        Direction dir = state.getValue(FACING);
        BlockPos attach = pos.relative(dir.getOpposite());
        return Block.canSupportCenter(level, attach, dir);
    }

    @Override
    public @NotNull BlockState updateShape(
            BlockState state,
            @NotNull Direction facing,
            @NotNull BlockState facingState,
            @NotNull LevelAccessor level,
            @NotNull BlockPos currentPos,
            @NotNull BlockPos facingPos
    ) {
        return canSurvive(state, level, currentPos) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    public @NotNull VoxelShape getShape(
            BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext ctx
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        int ext = be instanceof JinguBangRodBlockEntity rod ? rod.getExtendGrid() : 0;
        return RodShapes.shape(state.getValue(FACING), ext);
    }

    @Override
    public @NotNull InteractionResult use(
            BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hit
    ) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof JinguBangRodBlockEntity rod) {
            rod.tryExtendAlong(level, pos, state.getValue(FACING));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(
            BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof JinguBangRodBlockEntity rod) {
                ItemStack drop = new ItemStack(ModItems.RUYI_JINGU_BANG.get());
                RuyiJinguBangItem.setExtend(drop, rod.getExtendGrid());
                net.minecraft.world.Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder builder) {
        return Collections.emptyList();
    }

    /** 沿轴向拼接的细长碰撞盒（单方块坐标系内可超出 0–16）。 */
    static final class RodShapes {
        private static final double R0 = 6.0;
        private static final double R1 = 10.0;

        static VoxelShape shape(Direction facing, int extend) {
            List<VoxelShape> parts = new ArrayList<>();
            for (int i = 0; i <= extend; i++) {
                parts.add(segment(facing, i));
            }
            VoxelShape acc = parts.get(0);
            for (int i = 1; i < parts.size(); i++) {
                acc = Shapes.or(acc, parts.get(i));
            }
            return acc;
        }

        private static VoxelShape segment(Direction facing, int index) {
            double minX = R0, minY = R0, minZ = R0;
            double maxX = R1, maxY = R1, maxZ = R1;
            double off = index * 16.0;
            switch (facing) {
                case DOWN -> {
                    minY -= off;
                    maxY -= off;
                }
                case UP -> {
                    minY += off;
                    maxY += off;
                }
                case NORTH -> {
                    minZ -= off;
                    maxZ -= off;
                }
                case SOUTH -> {
                    minZ += off;
                    maxZ += off;
                }
                case WEST -> {
                    minX -= off;
                    maxX -= off;
                }
                case EAST -> {
                    minX += off;
                    maxX += off;
                }
            }
            return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
}
