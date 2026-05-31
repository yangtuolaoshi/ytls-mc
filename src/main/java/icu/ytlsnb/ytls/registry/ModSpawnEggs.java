package icu.ytlsnb.ytls.registry;

import icu.ytlsnb.ytls.ModConstants;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSpawnEggs {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModConstants.MOD_ID);

    public static final RegistryObject<Item> HOMELANDER_SPAWN_EGG = ITEMS.register("homelander_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntityTypes.HOMELANDER, 0xE6E6E6, 0xB30000, new Item.Properties()));

    private ModSpawnEggs() {
    }
}
