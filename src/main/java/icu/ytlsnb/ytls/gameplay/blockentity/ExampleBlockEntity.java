package icu.ytlsnb.ytls.gameplay.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 示例方块实体，由 {@link icu.ytlsnb.ytls.gameplay.registry.ExampleGameplayRegistryContributor} 注册类型。
 */
public class ExampleBlockEntity extends BlockEntity {
    public ExampleBlockEntity(BlockEntityType<ExampleBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
