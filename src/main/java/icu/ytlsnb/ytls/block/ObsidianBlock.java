package icu.ytlsnb.ytls.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ObsidianBlock extends Block {
    public ObsidianBlock() {
        super(
                BlockBehaviour.Properties
                        .of()
                        .strength(20F, 1500F)// 硬度和爆炸抗性
        );
    }
}
