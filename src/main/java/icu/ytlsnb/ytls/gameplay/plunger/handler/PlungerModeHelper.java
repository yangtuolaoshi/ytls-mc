package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 马桶塞模式与吸声音 NBT。
 */
public final class PlungerModeHelper {
    public static final String TAG_MODE = "PlungerMode";
    public static final String TAG_SOUNDS = "StoredSounds";
    public static final String MODE_NORMAL = "normal";
    public static final String MODE_SOUND = "sound";

    private PlungerModeHelper() {
    }

    public static boolean isSoundMode(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && MODE_SOUND.equals(tag.getString(TAG_MODE));
    }

    public static void setMode(ItemStack stack, String mode) {
        stack.getOrCreateTag().putString(TAG_MODE, mode);
    }

    public static void toggleSoundMode(ItemStack stack) {
        if (isSoundMode(stack)) {
            setMode(stack, MODE_NORMAL);
            clearSounds(stack);
        } else {
            setMode(stack, MODE_SOUND);
        }
    }

    public static void clearSounds(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(TAG_SOUNDS);
        }
    }

    public static void storeSounds(ItemStack stack, List<ResourceLocation> sounds) {
        ListTag list = new ListTag();
        for (ResourceLocation id : sounds) {
            list.add(StringTag.valueOf(id.toString()));
        }
        stack.getOrCreateTag().put(TAG_SOUNDS, list);
    }

    public static List<ResourceLocation> getStoredSounds(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_SOUNDS, Tag.TAG_LIST)) {
            return Collections.emptyList();
        }
        ListTag list = tag.getList(TAG_SOUNDS, Tag.TAG_STRING);
        List<ResourceLocation> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(list.getString(i));
            if (id != null) {
                result.add(id);
            }
        }
        return result;
    }

    public static boolean hasStoredSounds(ItemStack stack) {
        return !getStoredSounds(stack).isEmpty();
    }
}
