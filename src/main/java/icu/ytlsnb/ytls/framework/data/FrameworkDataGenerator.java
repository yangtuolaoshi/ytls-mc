package icu.ytlsnb.ytls.framework.data;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.data.provider.AutoBlockStateProvider;
import icu.ytlsnb.ytls.framework.data.provider.AutoItemModelProvider;
import icu.ytlsnb.ytls.framework.data.provider.AutoLangProvider;
import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

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

        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new AutoLangProvider(packOutput, provider));
        generator.addProvider(event.includeClient(), new AutoBlockStateProvider(packOutput, provider, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new AutoItemModelProvider(packOutput, provider, event.getExistingFileHelper()));

        LOG.info("Registered framework data providers");
    }
}
