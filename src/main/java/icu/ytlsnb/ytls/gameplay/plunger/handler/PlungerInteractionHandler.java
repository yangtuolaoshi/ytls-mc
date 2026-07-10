package icu.ytlsnb.ytls.gameplay.plunger.handler;

import icu.ytlsnb.ytls.framework.event.annotation.Listen;
import icu.ytlsnb.ytls.framework.event.api.GameEventType;
import icu.ytlsnb.ytls.framework.event.events.EntityInteractEvent;
import icu.ytlsnb.ytls.framework.event.events.RightClickBlockEvent;
import icu.ytlsnb.ytls.gameplay.plunger.item.PlungerItem;
import icu.ytlsnb.ytls.gameplay.toilet.block.ToiletBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 抢在原版方块/实体交互之前处理马桶塞，避免箱子开 GUI、床睡觉、村民交易等抢占。
 */
public final class PlungerInteractionHandler {
    /** 同一玩家同一 tick 内 EntityInteract / EntityInteractSpecific 可能各触发一次 */
    private static final Map<UUID, Long> ENTITY_HANDLED_TICK = new HashMap<>();

    private PlungerInteractionHandler() {
    }

    @Listen(GameEventType.RIGHT_CLICK_BLOCK)
    public static void onRightClickBlock(RightClickBlockEvent event) {
        ItemStack stack = event.stack();
        if (!(stack.getItem() instanceof PlungerItem)) {
            return;
        }
        Player player = event.player();
        // 吸附中：任意右键方块都发射，不处理方块
        if (PlungerSuctionHandler.hasSuckedEntity(stack)) {
            event.setDenyBlockUse(true);
            event.setForceItemUse(true);
            return;
        }
        // Shift 留给实体吸附；此时不拦截方块
        if (player.isShiftKeyDown()) {
            return;
        }
        BlockState state = event.state();
        if (!shouldOverrideBlockUse(state)) {
            return;
        }
        // 禁止箱子/床等 Block.use()，强制走 Item.useOn()
        event.setDenyBlockUse(true);
        event.setForceItemUse(true);
    }

    @Listen(GameEventType.ENTITY_INTERACT)
    public static void onEntityInteract(EntityInteractEvent event) {
        ItemStack stack = event.stack();
        if (!(stack.getItem() instanceof PlungerItem)) {
            return;
        }
        // 客户端也取消，避免本地打开村民交易等 UI
        if (event.level().isClientSide()) {
            event.setCancelled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }
        Player player = event.player();
        if (!markEntityHandled(player)) {
            event.setCancelled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        // 吸附中：任意右键（含点到其他生物）都发射，不再造成伤害
        if (PlungerSuctionHandler.hasSuckedEntity(stack)) {
            boolean launched = PlungerSuctionHandler.launchSucked(player, stack);
            event.setCancelled(true);
            event.setCancellationResult(launched ? InteractionResult.SUCCESS : InteractionResult.FAIL);
            return;
        }

        if (!(event.target() instanceof LivingEntity living) || !living.isAlive()) {
            return;
        }

        boolean success;
        if (player.isShiftKeyDown()) {
            success = PlungerSuctionHandler.tryCapture(player, stack, living);
        } else {
            success = PlungerEntityHandler.handle(player, stack, living);
        }

        // 无论成败都接管交互，避免继续走到村民交易 / 二次 interactLivingEntity
        event.setCancelled(true);
        event.setCancellationResult(success ? InteractionResult.SUCCESS : InteractionResult.FAIL);
    }

    private static boolean markEntityHandled(Player player) {
        long tick = player.level().getGameTime();
        Long previous = ENTITY_HANDLED_TICK.put(player.getUUID(), tick);
        return previous == null || previous != tick;
    }

    /**
     * 仅拦截会消费右键的交互型方块（开 GUI / 睡觉等），普通方块仍走原版流程 + useOn。
     */
    private static boolean shouldOverrideBlockUse(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof ToiletBlock) {
            return true;
        }
        if (block instanceof ChestBlock
                || block instanceof AbstractFurnaceBlock
                || block instanceof BarrelBlock
                || block instanceof DispenserBlock
                || block instanceof DropperBlock) {
            return true;
        }
        return block instanceof BedBlock && block != Blocks.WHITE_BED;
    }
}
