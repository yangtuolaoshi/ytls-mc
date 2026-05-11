package icu.ytlsnb.ytls.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 仙丹：半格饱食度（nutrition=1），随机一种原版效果。
 */
public class XianDanItem extends Item {
    public XianDanItem() {
        super(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2F).alwaysEat().build()));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && entity instanceof net.minecraft.world.entity.player.Player) {
            MobEffect effect = randomVanillaEffect(level.random);
            if (effect != null) {
                entity.addEffect(new MobEffectInstance(effect, 200 + level.getRandom().nextInt(400), level.getRandom().nextInt(2)));
            }
        }
        return stack;
    }

    private static MobEffect randomVanillaEffect(RandomSource random) {
        List<MobEffect> list = new ArrayList<>();
        BuiltInRegistries.MOB_EFFECT.forEach(list::add);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }
}
