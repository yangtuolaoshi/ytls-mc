package icu.ytlsnb.ytls.gameplay.data;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class ToiletRecipeProvider extends RecipeProvider {
    public ToiletRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, RegistryAccess.item("toilet"))
                .pattern("  Q")
                .pattern("QQQ")
                .pattern("QQQ")
                .define('Q', Blocks.QUARTZ_BLOCK)
                .unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK))
                .save(output, ModResources.loc("toilet"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryAccess.item("plunger"))
                .pattern("LLL")
                .pattern(" S ")
                .pattern(" S ")
                .define('L', Items.LEATHER)
                .define('S', Items.STICK)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(output, ModResources.loc("plunger"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, RegistryAccess.item("plunger_gun"))
                .pattern("T  ")
                .pattern("IIP")
                .pattern("I  ")
                .define('T', Items.TRIPWIRE_HOOK)
                .define('I', Items.IRON_INGOT)
                .define('P', RegistryAccess.item("plunger"))
                .unlockedBy("has_plunger", has(RegistryAccess.item("plunger")))
                .save(output, ModResources.loc("plunger_gun"));
    }
}
