package icu.ytlsnb.ytls.common;

import icu.ytlsnb.ytls.core.ModConstants;
import icu.ytlsnb.ytls.init.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 向原版创造标签追加条目；与 {@link icu.ytlsnb.ytls.init.ModCreativeTabs} 中的自定义标签互补。
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CreativeTabVanillaInjection {
    private CreativeTabVanillaInjection() {
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.OBSIDIAN_INGOT.get());
        }
    }
}
