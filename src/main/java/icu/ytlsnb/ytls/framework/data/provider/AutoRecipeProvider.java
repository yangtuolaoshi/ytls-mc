package icu.ytlsnb.ytls.framework.data.provider;

import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

/**
 * 为已注册的方块/物品生成基础配方（方块 3x3 → 9 个同名物品，若物品名与方块对应）。
 */
public final class AutoRecipeProvider extends RecipeProvider {
    private final ForgeRegistryProvider registry;

    public AutoRecipeProvider(PackOutput output, ForgeRegistryProvider registry) {
        super(output);
        this.registry = registry;
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        for (var blockEntry : registry.catalog()) {
            if (blockEntry.kind() != RegistryKind.BLOCK) {
                continue;
            }
            String blockName = blockEntry.name();
            Optional<String> itemName = findCompanionItem(blockName);
            if (itemName.isEmpty()) {
                continue;
            }
            Block block = RegistryAccess.block(blockName);
            Item item = RegistryAccess.item(itemName.get());
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item, 9)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', block)
                    .unlockedBy("has_" + blockName, has(block))
                    .save(output, ModResources.loc(blockName + "_from_block"));
        }
    }

    /**
     * 约定：方块 {@code example_block} 对应物品 {@code example_ingot}（去掉 _block 后缀 + _ingot）。
     */
    private Optional<String> findCompanionItem(String blockName) {
        if (blockName.endsWith("_block")) {
            String base = blockName.substring(0, blockName.length() - "_block".length());
            String ingotName = base + "_ingot";
            if (registry.get(RegistryKind.ITEM, ingotName).isPresent()) {
                return Optional.of(ingotName);
            }
        }
        return Optional.empty();
    }
}
