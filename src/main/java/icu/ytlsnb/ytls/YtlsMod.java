package icu.ytlsnb.ytls;

import icu.ytlsnb.ytls.command.ModCommands;
import icu.ytlsnb.ytls.event.ModEvents;
import icu.ytlsnb.ytls.registry.ModBlockEntities;
import icu.ytlsnb.ytls.registry.ModBlocks;
import icu.ytlsnb.ytls.registry.ModCreativeTabHandler;
import icu.ytlsnb.ytls.registry.ModCreativeTabs;
import icu.ytlsnb.ytls.registry.ModEntityTypes;
import icu.ytlsnb.ytls.registry.ModFluids;
import icu.ytlsnb.ytls.registry.ModItems;
import icu.ytlsnb.ytls.registry.ModMenuTypes;
import icu.ytlsnb.ytls.registry.ModParticles;
import icu.ytlsnb.ytls.registry.ModSounds;
import icu.ytlsnb.ytls.registry.ModSpawnEggs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModConstants.MOD_ID)
public final class YtlsMod {
    public YtlsMod() {
        ModFluids.bootstrap();
        ModBlocks.bootstrap();
        ModItems.bootstrap();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModSpawnEggs.ITEMS.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(ModCreativeTabHandler::onBuildCreativeTabs);
        modEventBus.addListener(ModEvents::onAttributes);

        MinecraftForge.EVENT_BUS.addListener(ModCommands::register);
    }
}
