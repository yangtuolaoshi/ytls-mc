package icu.ytlsnb.ytls.framework.registry.api;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * 框架支持的注册类型。扩展新类型时在此枚举增加项并实现 {@link icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider} 绑定即可。
 */
public enum RegistryKind {
    BLOCK(ForgeRegistries.BLOCKS, Block.class),
    ITEM(ForgeRegistries.ITEMS, Item.class),
    ENTITY(ForgeRegistries.ENTITY_TYPES, EntityType.class),
    BLOCK_ENTITY(ForgeRegistries.BLOCK_ENTITY_TYPES, BlockEntityType.class),
    SOUND(ForgeRegistries.SOUND_EVENTS, SoundEvent.class),
    MENU(ForgeRegistries.MENU_TYPES, MenuType.class),
    MOB_EFFECT(ForgeRegistries.MOB_EFFECTS, MobEffect.class),
    PARTICLE(ForgeRegistries.PARTICLE_TYPES, ParticleType.class),
    ENCHANTMENT(ForgeRegistries.ENCHANTMENTS, Enchantment.class),
    ATTRIBUTE(ForgeRegistries.ATTRIBUTES, Attribute.class),
    /** 使用 {@link Registries#CREATIVE_MODE_TAB}，无 ForgeRegistries 常量 */
    CREATIVE_TAB(null, CreativeModeTab.class),
    RECIPE_SERIALIZER(ForgeRegistries.RECIPE_SERIALIZERS, RecipeSerializer.class);

    private final IForgeRegistry<?> forgeRegistry;
    private final Class<?> entryClass;

    RegistryKind(IForgeRegistry<?> forgeRegistry, Class<?> entryClass) {
        this.forgeRegistry = forgeRegistry;
        this.entryClass = entryClass;
    }

    @SuppressWarnings("unchecked")
    public <T> IForgeRegistry<T> forgeRegistry() {
        if (forgeRegistry == null) {
            throw new UnsupportedOperationException(
                    "Registry kind " + name() + " uses vanilla Registries; use resourceKey() with DeferredRegister");
        }
        return (IForgeRegistry<T>) forgeRegistry;
    }

    public boolean usesVanillaRegistry() {
        return forgeRegistry == null;
    }

    public Class<?> entryClass() {
        return entryClass;
    }

    /**
     * 用于数据生成等需要原版 RegistryKey 的场景。
     */
    public ResourceKey<? extends net.minecraft.core.Registry<?>> resourceKey() {
        return switch (this) {
            case BLOCK -> Registries.BLOCK;
            case ITEM -> Registries.ITEM;
            case ENTITY -> Registries.ENTITY_TYPE;
            case BLOCK_ENTITY -> Registries.BLOCK_ENTITY_TYPE;
            case SOUND -> Registries.SOUND_EVENT;
            case MENU -> Registries.MENU;
            case MOB_EFFECT -> Registries.MOB_EFFECT;
            case PARTICLE -> Registries.PARTICLE_TYPE;
            case ENCHANTMENT -> Registries.ENCHANTMENT;
            case ATTRIBUTE -> Registries.ATTRIBUTE;
            case CREATIVE_TAB -> Registries.CREATIVE_MODE_TAB;
            case RECIPE_SERIALIZER -> Registries.RECIPE_SERIALIZER;
        };
    }
}
