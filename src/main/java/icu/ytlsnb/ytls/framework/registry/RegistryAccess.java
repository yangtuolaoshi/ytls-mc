package icu.ytlsnb.ytls.framework.registry;

import icu.ytlsnb.ytls.framework.registry.api.RegistryFacade;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import net.minecraft.core.particles.ParticleType;
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

/**
 * 业务层访问已注册对象的便捷入口，避免直接操作 RegistryObject。
 */
public final class RegistryAccess {
    private static ForgeRegistryProvider provider;

    private RegistryAccess() {
    }

    public static void bind(ForgeRegistryProvider forgeProvider) {
        provider = forgeProvider;
    }

    public static Block block(String name) {
        return resolve(RegistryKind.BLOCK, name);
    }

    public static Item item(String name) {
        return resolve(RegistryKind.ITEM, name);
    }

    public static SoundEvent sound(String name) {
        return resolve(RegistryKind.SOUND, name);
    }

    public static EntityType<?> entity(String name) {
        return resolve(RegistryKind.ENTITY, name);
    }

    public static BlockEntityType<?> blockEntity(String name) {
        return resolve(RegistryKind.BLOCK_ENTITY, name);
    }

    public static MenuType<?> menu(String name) {
        return resolve(RegistryKind.MENU, name);
    }

    public static MobEffect mobEffect(String name) {
        return resolve(RegistryKind.MOB_EFFECT, name);
    }

    public static ParticleType<?> particle(String name) {
        return resolve(RegistryKind.PARTICLE, name);
    }

    public static Enchantment enchantment(String name) {
        return resolve(RegistryKind.ENCHANTMENT, name);
    }

    public static Attribute attribute(String name) {
        return resolve(RegistryKind.ATTRIBUTE, name);
    }

    public static CreativeModeTab creativeTab(String name) {
        return resolve(RegistryKind.CREATIVE_TAB, name);
    }

    public static RecipeSerializer<?> recipeSerializer(String name) {
        return resolve(RegistryKind.RECIPE_SERIALIZER, name);
    }

    public static <T> T resolve(RegistryKind kind, String name) {
        ensureProvider();
        try {
            return provider.resolve(kind, name);
        } catch (IllegalStateException ex) {
            throw missingEntry(kind, name, ex);
        }
    }

    public static RegistryFacade facade() {
        ensureProvider();
        return provider;
    }

    public static ForgeRegistryProvider provider() {
        ensureProvider();
        return provider;
    }

    private static void ensureProvider() {
        if (provider == null) {
            throw new IllegalStateException("RegistryAccess not initialized. Is Framework.initialize() called?");
        }
    }

    private static IllegalStateException missingEntry(RegistryKind kind, String name, IllegalStateException cause) {
        return new IllegalStateException(
                "Missing registry entry " + kind + "/" + name
                        + ". Add a @Register* annotation or RegistryContributor in the gameplay package.", cause);
    }
}
