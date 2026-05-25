package icu.ytlsnb.ytls.framework.registry.api;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * 框架支持的注册类型。扩展新类型时在此枚举增加项并实现 Forge 绑定即可。
 */
public enum RegistryKind {
    BLOCK(ForgeRegistries.BLOCKS, Block.class),
    ITEM(ForgeRegistries.ITEMS, Item.class),
    ENTITY(ForgeRegistries.ENTITY_TYPES, EntityType.class),
    BLOCK_ENTITY(ForgeRegistries.BLOCK_ENTITY_TYPES, BlockEntityType.class),
    SOUND(ForgeRegistries.SOUND_EVENTS, SoundEvent.class);

    private final IForgeRegistry<?> forgeRegistry;
    private final Class<?> entryClass;

    RegistryKind(IForgeRegistry<?> forgeRegistry, Class<?> entryClass) {
        this.forgeRegistry = forgeRegistry;
        this.entryClass = entryClass;
    }

    @SuppressWarnings("unchecked")
    public <T> IForgeRegistry<T> forgeRegistry() {
        return (IForgeRegistry<T>) forgeRegistry;
    }

    public Class<?> entryClass() {
        return entryClass;
    }
}
