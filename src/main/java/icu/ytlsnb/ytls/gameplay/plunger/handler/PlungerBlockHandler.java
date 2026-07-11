package icu.ytlsnb.ytls.gameplay.plunger.handler;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.gameplay.toilet.block.ToiletBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class PlungerBlockHandler {
    private PlungerBlockHandler() {
    }

    public static boolean suctionToilet(Level level, BlockPos pos, BlockState state, Player player, ItemStack plunger) {
        int count = ToiletBlock.getPoopCount(state);
        if (count <= 0) {
            return false;
        }
        if (!damagePlunger(plunger, player)) {
            return false;
        }
        ToiletBlock.setPoopCount(level, pos, count - 1);
        spawnItem(level, pos.above(), new ItemStack(RegistryAccess.item("poop")));
        return true;
    }

    public static boolean handleBlock(Level level, BlockPos pos, BlockState state, Player player, ItemStack plunger) {
        if (PlungerModeHelper.isSoundMode(plunger)) {
            return false;
        }
        if (PlungerFireHandler.trySuckFire(level, pos, state, player, plunger)) {
            return true;
        }
        if (!damagePlunger(plunger, player)) {
            return false;
        }
        int fortune = plunger.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE);
        if (PlungerContainerHandler.trySuckContainer(level, pos, state, player, fortune)) {
            return true;
        }
        if (PlungerCropHandler.trySuckCrop(level, pos, state, player, fortune)) {
            return true;
        }
        return PlungerTransformHandler.tryTransform(level, pos, state, player, fortune);
    }

    public static boolean damagePlunger(ItemStack plunger, Player player) {
        if (!(plunger.getItem() instanceof icu.ytlsnb.ytls.gameplay.plunger.item.PlungerItem)) {
            return false;
        }
        if (player.getAbilities().instabuild) {
            return true;
        }
        plunger.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        return true;
    }

    public static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ItemEntity entity = new ItemEntity(
                level,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                stack.copy()
        );
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
    }

    /** 矿物等：按堆叠复制，时运增加份数 */
    public static void spawnFortuneCopies(ServerLevel level, BlockPos pos, ItemStack stack, int fortune) {
        if (stack.isEmpty()) {
            return;
        }
        int copies = 1 + level.random.nextInt(1 + fortune);
        for (int i = 0; i < copies; i++) {
            spawnItem(level, pos, stack);
        }
    }

    /** @deprecated 使用 {@link #spawnFortuneCopies} 或 {@link #spawnItem} */
    public static void spawnDrops(ServerLevel level, BlockPos pos, ItemStack stack, int fortune) {
        spawnFortuneCopies(level, pos, stack, fortune);
    }
}
