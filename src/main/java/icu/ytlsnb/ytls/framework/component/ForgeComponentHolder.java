package icu.ytlsnb.ytls.framework.component;

import icu.ytlsnb.ytls.framework.component.api.ComponentHolder;
import icu.ytlsnb.ytls.framework.component.api.ComponentType;
import icu.ytlsnb.ytls.framework.component.api.GameComponent;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class ForgeComponentHolder implements ComponentHolder {
    private static final Logger LOG = FrameworkLog.of("component");

    private final Map<ResourceLocationKey, GameComponent> components = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GameComponent> Optional<T> get(ComponentType<T> type) {
        GameComponent component = components.get(new ResourceLocationKey(type.id()));
        if (component == null) {
            return Optional.empty();
        }
        return Optional.of((T) component);
    }

    @Override
    public <T extends GameComponent> T getOrCreate(ComponentType<T> type) {
        return get(type).orElseGet(() -> {
            T created = type.create();
            put(type, created);
            created.onAttach();
            return created;
        });
    }

    @Override
    public <T extends GameComponent> void put(ComponentType<T> type, T component) {
        components.put(new ResourceLocationKey(type.id()), component);
    }

    @Override
    public <T extends GameComponent> boolean has(ComponentType<T> type) {
        return components.containsKey(new ResourceLocationKey(type.id()));
    }

    @Override
    public void copyFrom(ComponentHolder other) {
        if (!(other instanceof ForgeComponentHolder forgeOther)) {
            return;
        }
        for (Map.Entry<ResourceLocationKey, GameComponent> entry : forgeOther.components.entrySet()) {
            GameComponent existing = components.get(entry.getKey());
            if (existing != null) {
                existing.copyFrom(entry.getValue());
            } else {
                ComponentType<?> type = ComponentRegistry.findById(entry.getKey().id().toString());
                if (type == null) {
                    continue;
                }
                GameComponent cloned = ComponentRegistry.create(type);
                cloned.copyFrom(entry.getValue());
                components.put(entry.getKey(), cloned);
                cloned.onAttach();
            }
        }
    }

    @Override
    public void clear() {
        components.values().forEach(GameComponent::onDetach);
        components.clear();
    }

    CompoundTag save() {
        CompoundTag root = new CompoundTag();
        components.forEach((key, component) -> {
            CompoundTag componentTag = new CompoundTag();
            component.save(componentTag);
            root.put(key.id().toString(), componentTag);
        });
        return root;
    }

    void load(CompoundTag root) {
        for (String key : root.getAllKeys()) {
            ComponentType<?> type = ComponentRegistry.findById(key);
            if (type == null) {
                LOG.warn("Unknown component id in NBT, skipped: {}", key);
                continue;
            }
            Tag tag = root.get(key);
            if (!(tag instanceof CompoundTag componentTag)) {
                continue;
            }
            GameComponent component = type.create();
            component.load(componentTag);
            components.put(new ResourceLocationKey(type.id()), component);
            component.onAttach();
        }
    }

    private record ResourceLocationKey(net.minecraft.resources.ResourceLocation id) {
    }
}
