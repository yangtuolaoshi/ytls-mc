package icu.ytlsnb.ytls.framework.data.provider;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * 为注册的方块添加基础挖掘标签。
 */
public final class AutoBlockTagProvider extends BlockTagsProvider {
    private final ForgeRegistryProvider registry;

    public AutoBlockTagProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            ForgeRegistryProvider registry,
            ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, ModConstants.MOD_ID, existingFileHelper);
        this.registry = registry;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var tag = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        for (var entry : registry.catalog()) {
            if (entry.kind() != RegistryKind.BLOCK) {
                continue;
            }
            Block block = RegistryAccess.block(entry.name());
            tag.add(block);
        }
    }
}
