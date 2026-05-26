package icu.ytlsnb.ytls.framework.component.api;

import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 组件类型标识，业务层通过 {@link icu.ytlsnb.ytls.framework.component.ComponentAccess} 访问。
 */
public final class ComponentType<T extends GameComponent> {
    private final ResourceLocation id;
    private final Class<T> componentClass;
    private final Supplier<T> factory;
    private final Set<ComponentTarget> targets;

    public ComponentType(String name, Class<T> componentClass, Supplier<T> factory, ComponentTarget... targets) {
        this.id = ModResources.loc(name);
        this.componentClass = componentClass;
        this.factory = factory;
        this.targets = targets.length == 0
                ? EnumSet.allOf(ComponentTarget.class)
                : EnumSet.copyOf(Set.of(targets));
    }

    public ResourceLocation id() {
        return id;
    }

    public Class<T> componentClass() {
        return componentClass;
    }

    public T create() {
        return factory.get();
    }

    public Set<ComponentTarget> targets() {
        return Set.copyOf(targets);
    }

    public boolean supports(ComponentTarget target) {
        return targets.contains(target);
    }
}
