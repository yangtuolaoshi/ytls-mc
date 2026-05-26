package icu.ytlsnb.ytls.framework.data;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.bootstrap.Framework;
import icu.ytlsnb.ytls.framework.bootstrap.LifecyclePhase;
import icu.ytlsnb.ytls.framework.data.provider.AutoBlockLootProvider;
import icu.ytlsnb.ytls.framework.data.provider.AutoBlockStateProvider;
import icu.ytlsnb.ytls.framework.data.provider.AutoBlockTagProvider;
import icu.ytlsnb.ytls.framework.data.provider.AutoItemModelProvider;
import icu.ytlsnb.ytls.framework.data.provider.AutoLangProvider;
import icu.ytlsnb.ytls.framework.data.provider.AutoRecipeProvider;
import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 数据生成入口，根据注册表目录自动生成基础资源。
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FrameworkDataGenerator {
    private static final Logger LOG = FrameworkLog.of("datagen");

    private FrameworkDataGenerator() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        ForgeRegistryProvider provider = RegistryAccess.provider();
        if (provider == null) {
            LOG.warn("Registry provider not initialized, skipping data generation");
            return;
        }

        if (Framework.isLoaded()) {
            Framework.get().lifecycle().fire(LifecyclePhase.DATA_GEN, event);
        }

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new AutoLangProvider(packOutput, provider));
        generator.addProvider(event.includeClient(), new AutoBlockStateProvider(packOutput, provider, existingFileHelper));
        generator.addProvider(event.includeClient(), new AutoItemModelProvider(packOutput, provider, existingFileHelper));

        generator.addProvider(event.includeServer(), new LootTableProvider(
                packOutput,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(
                        () -> new AutoBlockLootProvider(provider),
                        LootContextParamSets.BLOCK
                ))
        ));
        generator.addProvider(event.includeServer(), new AutoRecipeProvider(packOutput, provider));
        generator.addProvider(event.includeServer(), new AutoBlockTagProvider(
                packOutput, lookupProvider, provider, existingFileHelper));

        LOG.info("Registered framework data providers (lang, models, loot, recipe, tags)");
    }
}
