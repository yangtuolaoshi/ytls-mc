package icu.ytlsnb.ytls.gameplay.block;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

@RegisterBlock("example_block")
public class ExampleBlock extends Block {
    public ExampleBlock() {
        super(BlockBehaviour.Properties.of().strength(2.0F, 6.0F));
    }
}
