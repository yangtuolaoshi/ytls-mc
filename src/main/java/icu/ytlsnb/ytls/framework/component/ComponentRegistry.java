package icu.ytlsnb.ytls.framework.component;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.component.annotation.RegisterComponent;
import icu.ytlsnb.ytls.framework.component.api.ComponentTarget;
import icu.ytlsnb.ytls.framework.component.api.ComponentType;
import icu.ytlsnb.ytls.framework.component.api.GameComponent;
import icu.ytlsnb.ytls.framework.util.ClasspathScanner;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ComponentRegistry {
    private static final Logger LOG = FrameworkLog.of("component-registry");
    private static final Map<ResourceLocation, ComponentType<?>> typesById = new HashMap<>();

    private ComponentRegistry() {
    }

    public static void scanAndRegister(String basePackage) {
        for (Class<?> clazz : ClasspathScanner.scanByAnnotation(basePackage, RegisterComponent.class)) {
            if (!GameComponent.class.isAssignableFrom(clazz)) {
                throw new IllegalStateException("@RegisterComponent must implement GameComponent: " + clazz.getName());
            }
            RegisterComponent meta = clazz.getAnnotation(RegisterComponent.class);
            registerType(meta.value(), meta.targets(), clazz);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends GameComponent> void registerType(String id, ComponentTarget[] targets, Class<?> clazz) {
        ResourceLocation location = ModResources.loc(id);
        if (typesById.containsKey(location)) {
            throw new IllegalStateException("Duplicate component type: " + id);
        }
        ComponentType<T> type = new ComponentType<>(id, () -> instantiate(clazz), targets);
        typesById.put(location, type);
        LOG.info("Registered component type: {}", location);
    }

    @SuppressWarnings("unchecked")
    public static <T extends GameComponent> ComponentType<T> type(String id) {
        ComponentType<?> type = typesById.get(ModResources.loc(id));
        if (type == null) {
            throw new IllegalStateException("Component type not found: " + id);
        }
        return (ComponentType<T>) type;
    }

    public static ComponentType<?> findById(String id) {
        if (id.contains(":")) {
            return typesById.get(new ResourceLocation(id));
        }
        return typesById.get(ModResources.loc(id));
    }

    public static Optional<ComponentType<?>> findOptional(String id) {
        return Optional.ofNullable(findById(id));
    }

    static GameComponent create(ComponentType<?> type) {
        return type.create();
    }

    public static Map<ResourceLocation, ComponentType<?>> allTypes() {
        return Map.copyOf(typesById);
    }

    private static <T extends GameComponent> T instantiate(Class<?> clazz) {
        try {
            Constructor<?> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            @SuppressWarnings("unchecked")
            T instance = (T) ctor.newInstance();
            return instance;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to instantiate component: " + clazz.getName(), ex);
        }
    }
}
