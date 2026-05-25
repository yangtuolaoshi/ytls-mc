package icu.ytlsnb.ytls.framework.data.provider;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.world.item.Item;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class AutoItemModelProvider extends ItemModelProvider {
    private final ForgeRegistryProvider registry;

    public AutoItemModelProvider(PackOutput output, ForgeRegistryProvider registry, ExistingFileHelper existingFileHelper) {
        super(output, ModConstants.MOD_ID, existingFileHelper);
        this.registry = registry;
    }

    @Override
    protected void registerModels() {
        for (var entry : registry.catalog()) {
            if (entry.kind() == RegistryKind.ITEM) {
                Item item = RegistryAccess.item(entry.name());
                basicItem(item);
                continue;
            }
            if (entry.kind() == RegistryKind.BLOCK) {
                withExistingParent(entry.name(), ModResources.loc("block/" + entry.name()));
            }
        }
    }
}
