package icu.ytlsnb.ytls.gameplay.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/** 示例状态效果，由 {@link icu.ytlsnb.ytls.gameplay.registry.ExampleMobEffectSupplier} 注册。 */
public final class ExampleBriskEffect extends MobEffect {
    public ExampleBriskEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x88CCFF);
    }
}
