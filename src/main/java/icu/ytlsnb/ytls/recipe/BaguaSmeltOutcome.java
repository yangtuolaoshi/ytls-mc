package icu.ytlsnb.ytls.recipe;

import icu.ytlsnb.ytls.init.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 炼制解析：成功产出 {@link #prize()}，否则产出炉渣。
 */
public final class BaguaSmeltOutcome {
    private final @Nullable Item prize;
    private final double successChance;

    private BaguaSmeltOutcome(@Nullable Item prize, double successChance) {
        this.prize = prize;
        this.successChance = successChance;
    }

    public @Nullable Item prize() {
        return prize;
    }

    public ItemStack produce(RandomSource random) {
        Item slag = ModItems.BAGUA_SLAG.get();
        if (prize != null && random.nextDouble() < successChance) {
            return new ItemStack(prize);
        }
        return new ItemStack(slag);
    }

    /**
     * 输出槽是否仍能容纳本次炼制可能出现的任意结果。
     */
    public boolean canAcceptCurrentOutput(ItemStack outputSlot) {
        Item slag = ModItems.BAGUA_SLAG.get();
        if (outputSlot.isEmpty()) {
            return true;
        }
        if (prize != null && ItemStack.isSameItemSameTags(outputSlot, new ItemStack(prize))) {
            return outputSlot.getCount() < outputSlot.getMaxStackSize();
        }
        if (ItemStack.isSameItemSameTags(outputSlot, new ItemStack(slag))) {
            return outputSlot.getCount() < outputSlot.getMaxStackSize();
        }
        return false;
    }

    public static @Nullable BaguaSmeltOutcome resolve(ItemStack m0, ItemStack m1, ItemStack m2) {
        Map<Item, Integer> counts = new HashMap<>();
        add(counts, m0);
        add(counts, m1);
        add(counts, m2);

        int totalItems = counts.values().stream().mapToInt(Integer::intValue).sum();
        if (totalItems == 0) {
            return null;
        }

        if (isThreeOf(Items.STICK, counts)) {
            return new BaguaSmeltOutcome(ModItems.RUYI_JINGU_BANG.get(), 0.01);
        }
        if (isThreeOf(Items.IRON_HOE, counts)) {
            return new BaguaSmeltOutcome(ModItems.JIUCHI_DINGPA.get(), 0.01);
        }
        if (counts.getOrDefault(Items.ROTTEN_FLESH, 0) >= 1
                && counts.getOrDefault(Items.BONE, 0) >= 1
                && counts.getOrDefault(Items.SPIDER_EYE, 0) >= 1
                && totalItems == 3) {
            return new BaguaSmeltOutcome(ModItems.XIAN_DAN.get(), 0.05);
        }
        if (matchesMiniBagua(counts, totalItems)) {
            return new BaguaSmeltOutcome(ModItems.MINI_BAGUA_FURNACE.get(), 0.10);
        }

        return new BaguaSmeltOutcome(null, 0.0);
    }

    private static boolean matchesMiniBagua(Map<Item, Integer> counts, int totalItems) {
        if (totalItems != 3) {
            return false;
        }
        boolean hasFurnace = counts.getOrDefault(ModItems.BAGUA_FURNACE.get(), 0) >= 1;
        boolean hasBreath = counts.getOrDefault(Items.DRAGON_BREATH, 0) >= 1;
        boolean hasShroom = counts.getOrDefault(Items.RED_MUSHROOM, 0) >= 1
                || counts.getOrDefault(Items.BROWN_MUSHROOM, 0) >= 1
                || counts.getOrDefault(Items.CRIMSON_FUNGUS, 0) >= 1
                || counts.getOrDefault(Items.WARPED_FUNGUS, 0) >= 1;
        return hasFurnace && hasBreath && hasShroom;
    }

    private static boolean isThreeOf(Item item, Map<Item, Integer> counts) {
        return counts.size() == 1 && counts.getOrDefault(item, 0) == 3;
    }

    private static void add(Map<Item, Integer> counts, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        Item item = stack.getItem();
        counts.merge(item, stack.getCount(), Integer::sum);
    }
}
