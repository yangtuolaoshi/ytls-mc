package icu.ytlsnb.ytls.framework.bootstrap;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.config.ConfigManager;
import icu.ytlsnb.ytls.framework.event.GameEventBus;
import icu.ytlsnb.ytls.framework.event.bridge.ForgeEventBridge;
import icu.ytlsnb.ytls.framework.network.NetworkChannel;
import icu.ytlsnb.ytls.framework.network.NetworkAccess;
import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.RegistryScanner;
import icu.ytlsnb.ytls.framework.sync.SyncHelper;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * 框架唯一入口。Mod 主类只需调用 {@link #initialize()}，其余能力由此处统一编排。
 */
public final class Framework {
    public static final String GAMEPLAY_PACKAGE = "icu.ytlsnb.ytls.gameplay";
    public static final String FRAMEWORK_PACKAGE = "icu.ytlsnb.ytls.framework";

    private static final Logger LOG = FrameworkLog.of("bootstrap");

    private static Framework instance;

    private final ForgeRegistryProvider registryProvider = new ForgeRegistryProvider();
    private final LifecycleManager lifecycleManager = new LifecycleManager(GAMEPLAY_PACKAGE);
    private final GameEventBus gameEventBus = new GameEventBus();
    private final NetworkChannel networkChannel = new NetworkChannel();
    private final ConfigManager configManager = new ConfigManager();
    private final ForgeEventBridge forgeEventBridge = new ForgeEventBridge(gameEventBus);

    private Framework(IEventBus modBus) {
        LOG.info("Initializing YTLS framework for mod '{}'", ModConstants.MOD_ID);

        // 1. 配置（需尽早注册）
        configManager.scanAndRegister(FRAMEWORK_PACKAGE);
        configManager.scanAndRegister(GAMEPLAY_PACKAGE);

        // 2. 注册表扫描与绑定
        RegistryScanner.scanAndRegister(GAMEPLAY_PACKAGE, registryProvider);
        RegistryAccess.bind(registryProvider);
        registryProvider.bindToModBus(modBus);

        // 3. 网络与同步
        networkChannel.scanAndRegister(GAMEPLAY_PACKAGE);
        networkChannel.scanAndRegister(FRAMEWORK_PACKAGE);
        NetworkAccess.bind(networkChannel);
        SyncHelper.bind(networkChannel);

        // 4. 生命周期与事件
        lifecycleManager.scanAndRegister();
        lifecycleManager.bindModBus(modBus);
        lifecycleManager.bindForgeBus(net.minecraftforge.common.MinecraftForge.EVENT_BUS);
        gameEventBus.scanAndRegister(GAMEPLAY_PACKAGE);
        forgeEventBridge.register(net.minecraftforge.common.MinecraftForge.EVENT_BUS);

        lifecycleManager.fire(LifecyclePhase.CONSTRUCT, null);

        LOG.info("Framework initialized. Gameplay scan root: {}", GAMEPLAY_PACKAGE);
    }

    public static void initialize() {
        if (instance != null) {
            throw new IllegalStateException("Framework already initialized");
        }
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        instance = new Framework(modBus);
    }

    public static Framework get() {
        if (instance == null) {
            throw new IllegalStateException("Framework not initialized");
        }
        return instance;
    }

    public ForgeRegistryProvider registry() {
        return registryProvider;
    }

    public GameEventBus events() {
        return gameEventBus;
    }

    public NetworkChannel network() {
        return networkChannel;
    }

    public ConfigManager configs() {
        return configManager;
    }

    public LifecycleManager lifecycle() {
        return lifecycleManager;
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(ModConstants.MOD_ID);
    }
}
