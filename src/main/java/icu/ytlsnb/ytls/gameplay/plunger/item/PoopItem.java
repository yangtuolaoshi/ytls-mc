package icu.ytlsnb.ytls.gameplay.plunger.item;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

@RegisterItem("poop")
public class PoopItem extends Item {
    public PoopItem() {
        super(new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(2)
                .saturationMod(0.1F)
                .alwaysEat()
                .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 2), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 200, 2), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 2), 1.0F)
                .build()));
    }
}
