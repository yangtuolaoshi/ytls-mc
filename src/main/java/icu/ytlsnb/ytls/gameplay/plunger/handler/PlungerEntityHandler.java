package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class PlungerEntityHandler {
    private PlungerEntityHandler() {
    }

    public static boolean handle(Player player, ItemStack plunger, LivingEntity target) {
        if (!PlungerBlockHandler.damagePlunger(plunger, player)) {
            return false;
        }
        // 无敌帧内 hurt 会失败：此时不掉落，保证「受伤次数 = 掉落次数」
        boolean damaged = target.hurt(player.damageSources().playerAttack(player), 3.0F);
        if (!damaged || !(player.level() instanceof ServerLevel serverLevel)) {
            return true;
        }

        int looting = plunger.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.MOB_LOOTING);
        if (target instanceof AbstractVillager) {
            ItemStack emerald = new ItemStack(Items.EMERALD, 1 + serverLevel.random.nextInt(1 + looting));
            PlungerBlockHandler.spawnItem(serverLevel, target.blockPosition(), emerald);
            return true;
        }
        if (target instanceof AbstractFish) {
            return true;
        }
        dropDeathLoot(serverLevel, player, target, looting);
        return true;
    }

    private static void dropDeathLoot(ServerLevel level, Player player, LivingEntity target, int looting) {
        LootTable table = level.getServer().getLootData().getLootTable(target.getLootTable());
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, target)
                .withParameter(LootContextParams.ORIGIN, target.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, player.damageSources().playerAttack(player))
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                .withLuck(player.getLuck() + looting)
                .create(LootContextParamSets.ENTITY);
        // 直接生成战利品，不再对每个堆叠做时运倍增，避免一次吸出大量物品
        table.getRandomItems(params, stack ->
                PlungerBlockHandler.spawnItem(level, target.blockPosition(), stack));
    }
}
