package icu.ytlsnb.ytls.gameplay.toilet.block;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.gameplay.toilet.blockentity.ToiletBlockEntity;
import icu.ytlsnb.ytls.gameplay.toilet.entity.ToiletSeatEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@icu.ytlsnb.ytls.framework.registry.annotation.RegisterBlock("toilet")
public class ToiletBlock extends BaseEntityBlock {
    public static final MapCodec<ToiletBlock> CODEC = simpleCodec(ToiletBlock::new);
    public static final IntegerProperty POOP_COUNT = IntegerProperty.create("poop_count", 0, 3);

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(2, 0, 2, 14, 6, 14),
            Block.box(3, 6, 3, 13, 10, 13),
            Block.box(4, 10, 8, 12, 14, 14)
    );

    private static BlockBehaviour.Properties defaultProperties() {
        return BlockBehaviour.Properties.of()
                .strength(2.0F, 6.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion();
    }

    public ToiletBlock() {
        this(defaultProperties());
    }

    public ToiletBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(POOP_COUNT, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POOP_COUNT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ToiletBlockEntity(RegistryAccess.blockEntity("toilet_be"), pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.SUCCESS;
        }
        if (ToiletSeatEntity.findSeatAt(level, pos) != null) {
            return InteractionResult.CONSUME;
        }
        ToiletSeatEntity seat = ToiletSeatEntity.create(level, pos);
        if (seat != null) {
            player.startRiding(seat);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    public static void setPoopCount(Level level, BlockPos pos, int count) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof ToiletBlock) {
            level.setBlock(pos, state.setValue(POOP_COUNT, Math.max(0, Math.min(3, count))), Block.UPDATE_ALL);
        }
    }

    public static int getPoopCount(BlockState state) {
        return state.hasProperty(POOP_COUNT) ? state.getValue(POOP_COUNT) : 0;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof ToiletBlockEntity toilet) {
                ToiletBlockEntity.serverTick(lvl, pos, st, toilet);
            }
        };
    }
}
