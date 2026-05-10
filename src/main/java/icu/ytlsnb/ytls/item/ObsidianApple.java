package icu.ytlsnb.ytls.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class ObsidianApple extends Item {
    public static final FoodProperties foodProperties = new FoodProperties.Builder()
            .saturationMod(10)
            .nutrition(10)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000), 1.0f)
            .build();

    public ObsidianApple() {
        super(
                new Item.Properties()
                        .food(foodProperties)
        );
    }
}
