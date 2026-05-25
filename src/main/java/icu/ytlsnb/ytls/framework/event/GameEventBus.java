package icu.ytlsnb.ytls.framework.event;

import icu.ytlsnb.ytls.framework.event.annotation.Listen;
import icu.ytlsnb.ytls.framework.event.api.GameEvent;
import icu.ytlsnb.ytls.framework.event.api.GameEventPriority;
import icu.ytlsnb.ytls.framework.event.api.GameEventType;
import icu.ytlsnb.ytls.framework.util.ClasspathScanner;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 框架内部语义事件总线，业务层通过 {@link Listen} 注解订阅。
 */
public final class GameEventBus {
    private static final Logger LOG = FrameworkLog.of("event");

    private final Map<GameEventType, List<EventHandler>> handlers = new EnumMap<>(GameEventType.class);

    public GameEventBus() {
        for (GameEventType type : GameEventType.values()) {
            handlers.put(type, new ArrayList<>());
        }
    }

    public void scanAndRegister(String basePackage) {
        for (Class<?> clazz : ClasspathScanner.scanPackage(basePackage)) {
            for (Method method : clazz.getDeclaredMethods()) {
                Listen listen = method.getAnnotation(Listen.class);
                registerHandler(clazz, method, listen);
            }
        }
        handlers.values().forEach(list ->
                list.sort(Comparator.comparingInt(h -> h.priority.weight())));
    }

    private void registerHandler(Class<?> clazz, Method method, Listen listen) {
        if (listen == null) {
            return;
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("@Listen method must be static: " + clazz.getName() + "#" + method.getName());
        }
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 1 || !GameEvent.class.isAssignableFrom(params[0])) {
            throw new IllegalStateException(
                    "@Listen method must accept exactly one GameEvent parameter: " + clazz.getName() + "#" + method.getName());
        }
        method.setAccessible(true);
        handlers.get(listen.value()).add(new EventHandler(clazz, method, listen.priority()));
        LOG.info("Registered event listener: {}.{} -> {}", clazz.getSimpleName(), method.getName(), listen.value());
    }

    public void post(GameEventType type, GameEvent event) {
        for (EventHandler handler : handlers.get(type)) {
            try {
                handler.method.invoke(null, event);
                if (event.isCancelled()) {
                    break;
                }
            } catch (Exception ex) {
                throw new IllegalStateException(
                        "Event handler failed: " + handler.owner.getName() + "#" + handler.method.getName(), ex);
            }
        }
    }

    private record EventHandler(Class<?> owner, Method method, GameEventPriority priority) {
    }
}
