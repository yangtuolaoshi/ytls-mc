package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.npc.Villager;
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
        int looting = plunger.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.MOB_LOOTING);
        target.hurt(player.damageSources().generic(), 3.0F);

        if (player.level() instanceof ServerLevel serverLevel) {
            if (target instanceof Villager) {
                PlungerBlockHandler.spawnDrops(serverLevel, target.blockPosition(), new ItemStack(Items.EMERALD), looting);
            } else if (!(target instanceof AbstractFish) && !(target instanceof AbstractSchoolingFish)) {
                dropDeathLoot(serverLevel, player, target, looting);
            }
        }
        return true;
    }

    private static void dropDeathLoot(ServerLevel level, Player player, LivingEntity target, int looting) {
        LootTable table = level.getServer().getLootData().getLootTable(target.getLootTable());
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, target)
                .withParameter(LootContextParams.ORIGIN, target.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, player.damageSources().generic())
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                .withLuck(player.getLuck() + looting)
                .create(LootContextParamSets.ENTITY);
        table.getRandomItems(params, stack ->
                PlungerBlockHandler.spawnDrops(level, target.blockPosition(), stack, looting));
    }
}
