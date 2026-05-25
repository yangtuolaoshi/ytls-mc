package icu.ytlsnb.ytls.framework.data.provider;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class AutoBlockStateProvider extends BlockStateProvider {
    private final ForgeRegistryProvider registry;

    public AutoBlockStateProvider(PackOutput output, ForgeRegistryProvider registry, ExistingFileHelper existingFileHelper) {
        super(output, ModConstants.MOD_ID, existingFileHelper);
        this.registry = registry;
    }

    @Override
    protected void registerStatesAndModels() {
        for (var entry : registry.catalog()) {
            if (entry.kind() != RegistryKind.BLOCK) {
                continue;
            }
            Block block = RegistryAccess.block(entry.name());
            simpleBlock(block, models().cubeAll(entry.name(),
                    ModResources.loc("block/" + entry.name())));
        }
    }
}
