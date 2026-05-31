package icu.ytlsnb.ytls.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public final class ModCreativeTabHandler {

    private ModCreativeTabHandler() {
    }

    public static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.MILK_ALTAR_ITEM.get());
        }
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            ModItems.MILK_BUCKETS.values().forEach(event::accept);
            event.accept(ModItems.GALACTAGOGUE.get());
        }
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModSpawnEggs.HOMELANDER_SPAWN_EGG.get());
        }
    }
}
