package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * 吸声音模式：收集实体相关 SoundEvent，对空右键随机播放。
 */
public final class PlungerSoundHandler {
    private PlungerSoundHandler() {
    }

    public static boolean trySuckSounds(Player player, ItemStack plunger, Entity target) {
        if (!PlungerModeHelper.isSoundMode(plunger)) {
            return false;
        }
        List<ResourceLocation> sounds = collectEntitySounds(target);
        if (sounds.isEmpty()) {
            return false;
        }
        if (!PlungerBlockHandler.damagePlunger(plunger, player)) {
            return false;
        }
        PlungerModeHelper.storeSounds(plunger, sounds);
        return true;
    }

    public static boolean tryPlayStoredSound(Level level, Player player, ItemStack plunger) {
        if (!PlungerModeHelper.isSoundMode(plunger)) {
            return false;
        }
        List<ResourceLocation> sounds = PlungerModeHelper.getStoredSounds(plunger);
        if (sounds.isEmpty()) {
            return false;
        }
        ResourceLocation id = sounds.get(level.random.nextInt(sounds.size()));
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(id);
        if (sound == null) {
            return false;
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                sound, SoundSource.PLAYERS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
        return true;
    }

    /**
     * 收集该实体类型在注册表中「相关」的全部音效：
     * {@code entity.<path>.*} 以及 {@code entity.<namespace>.<path>.*}。
     */
    public static List<ResourceLocation> collectEntitySounds(Entity entity) {
        EntityType<?> type = entity.getType();
        ResourceLocation typeId = ForgeRegistries.ENTITY_TYPES.getKey(type);
        if (typeId == null) {
            typeId = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        }
        if (typeId == null) {
            return List.of();
        }

        String vanillaPrefix = "entity." + typeId.getPath() + ".";
        String namespacedPrefix = "entity." + typeId.getNamespace() + "." + typeId.getPath() + ".";

        List<ResourceLocation> result = new ArrayList<>();
        for (SoundEvent sound : ForgeRegistries.SOUND_EVENTS) {
            ResourceLocation id = ForgeRegistries.SOUND_EVENTS.getKey(sound);
            if (id == null) {
                continue;
            }
            String path = id.getPath();
            if (path.startsWith(vanillaPrefix) || path.startsWith(namespacedPrefix)) {
                result.add(id);
            }
        }
        return result;
    }
}
