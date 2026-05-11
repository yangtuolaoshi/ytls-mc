package icu.ytlsnb.ytls.item;

import icu.ytlsnb.ytls.init.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public enum ModItemTier implements Tier {
    OBSIDIAN(4, 5000, 11.0F, 5.0F, 18,
            Ingredient.of(ModItems.OBSIDIAN_INGOT.get())
    ),
    /** 如意金箍棒：高耐久、极高攻击力（数值可调）。 */
    JINGU_BANG(4, 65535, 1.0F, 14.0F, 25, Ingredient.EMPTY),
    /** 九齿钉耙：锄类兼顾战斗。 */
    JIUCHI_RAKE(4, 6000, 8.0F, 6.0F, 22, Ingredient.EMPTY);

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Ingredient repairIngredient;

    ModItemTier(int level, int uses, float speed, float damage, int enchantmentValue, Ingredient repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }
}
