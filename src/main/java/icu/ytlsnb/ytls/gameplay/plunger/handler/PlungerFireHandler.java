package icu.ytlsnb.ytls.gameplay.plunger.handler;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 吸火焰 → 获得火球物品。
 */
public final class PlungerFireHandler {
    private PlungerFireHandler() {
    }

    public static boolean isFire(BlockState state) {
        return state.getBlock() instanceof BaseFireBlock
                || state.is(Blocks.FIRE)
                || state.is(Blocks.SOUL_FIRE);
    }

    public static boolean trySuckFire(Level level, BlockPos pos, BlockState state, Player player, ItemStack plunger) {
        if (!isFire(state)) {
            return false;
        }
        if (PlungerModeHelper.isSoundMode(plunger)) {
            return false;
        }
        if (!PlungerBlockHandler.damagePlunger(plunger, player)) {
            return false;
        }
        level.removeBlock(pos, false);
        ItemStack fireball = new ItemStack(RegistryAccess.item("sucked_fireball"));
        if (!player.getInventory().add(fireball)) {
            PlungerBlockHandler.spawnItem(level, pos, fireball);
        }
        return true;
    }
}
