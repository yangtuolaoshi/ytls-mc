package icu.ytlsnb.ytls.framework.bootstrap;

import icu.ytlsnb.ytls.framework.bootstrap.annotation.OnLifecycle;
import icu.ytlsnb.ytls.framework.util.ClasspathScanner;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 统一封装 Forge/FML 生命周期，对外暴露语义化阶段回调。
 */
public final class LifecycleManager {
    private static final Logger LOG = FrameworkLog.of("lifecycle");

    private final String scanPackage;
    private final Map<LifecyclePhase, List<LifecycleHandler>> handlers = new EnumMap<>(LifecyclePhase.class);

    public LifecycleManager(String scanPackage) {
        this.scanPackage = scanPackage;
        for (LifecyclePhase phase : LifecyclePhase.values()) {
            handlers.put(phase, new ArrayList<>());
        }
    }

    public void scanAndRegister() {
        for (Class<?> clazz : ClasspathScanner.scanPackage(scanPackage)) {
            for (Method method : clazz.getDeclaredMethods()) {
                OnLifecycle annotation = method.getAnnotation(OnLifecycle.class);
                if (annotation == null) {
                    continue;
                }
                validateHandlerMethod(clazz, method);
                handlers.get(annotation.value()).add(new LifecycleHandler(clazz, method));
                LOG.info("Registered lifecycle handler: {}.{} -> {}", clazz.getSimpleName(), method.getName(), annotation.value());
            }
        }
    }

    private static void validateHandlerMethod(Class<?> clazz, Method method) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException(
                    "@OnLifecycle method must be static: " + clazz.getName() + "#" + method.getName());
        }
        Class<?>[] params = method.getParameterTypes();
        if (params.length > 1) {
            throw new IllegalStateException(
                    "@OnLifecycle method accepts at most one parameter: " + clazz.getName() + "#" + method.getName());
        }
        method.setAccessible(true);
    }

    public void bindModBus(IEventBus modBus) {
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onClientSetup);
    }

    public void bindForgeBus(IEventBus forgeBus) {
        forgeBus.addListener(this::onServerStarting);
        forgeBus.addListener(this::onServerStarted);
        forgeBus.addListener(this::onServerStopping);
        forgeBus.addListener(this::onWorldLoad);
        forgeBus.addListener(this::onWorldUnload);
    }

    public void fire(LifecyclePhase phase, Object context) {
        for (LifecycleHandler handler : handlers.get(phase)) {
            try {
                invokeHandler(handler, context);
            } catch (Exception ex) {
                throw new IllegalStateException(
                        "Lifecycle handler failed: " + handler.owner.getName() + "#" + handler.method.getName(), ex);
            }
        }
    }

    private static void invokeHandler(LifecycleHandler handler, Object context) throws ReflectiveOperationException {
        if (handler.method.getParameterCount() == 0) {
            handler.method.invoke(null);
            return;
        }
        if (context == null) {
            throw new IllegalStateException(
                    "Lifecycle handler requires a context argument: "
                            + handler.owner.getName() + "#" + handler.method.getName());
        }
        handler.method.invoke(null, context);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> fire(LifecyclePhase.COMMON_SETUP, event));
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        event.enqueueWork(() -> fire(LifecyclePhase.CLIENT_SETUP, event));
    }

    private void onServerStarting(ServerStartingEvent event) {
        fire(LifecyclePhase.SERVER_STARTING, event);
    }

    private void onServerStarted(ServerStartedEvent event) {
        fire(LifecyclePhase.SERVER_STARTED, event);
    }

    private void onServerStopping(ServerStoppingEvent event) {
        fire(LifecyclePhase.SERVER_STOPPING, event);
    }

    private void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            fire(LifecyclePhase.WORLD_LOAD, serverLevel);
        }
    }

    private void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            fire(LifecyclePhase.WORLD_UNLOAD, serverLevel);
        }
    }

    private record LifecycleHandler(Class<?> owner, Method method) {
    }
}
