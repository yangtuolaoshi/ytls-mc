package icu.ytlsnb.ytls.network;

import icu.ytlsnb.ytls.block.JinguBangRodBlock;
import icu.ytlsnb.ytls.block.entity.JinguBangRodBlockEntity;
import icu.ytlsnb.ytls.item.RuyiJinguBangItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * R 键：手持金箍棒重置伸长；对准放置金箍棒重置伸长。
 */
public final class BaguaActionPacket {
    public enum Action {
        RESET_HAND_JINGU,
        RESET_WORLD_JINGU
    }

    private final Action action;
    private final BlockPos pos;

    public BaguaActionPacket(Action action, BlockPos pos) {
        this.action = action;
        this.pos = pos;
    }

    public static void encode(BaguaActionPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.action);
        buf.writeBlockPos(msg.pos);
    }

    public static BaguaActionPacket decode(FriendlyByteBuf buf) {
        return new BaguaActionPacket(buf.readEnum(Action.class), buf.readBlockPos());
    }

    public static void handle(BaguaActionPacket msg, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) {
            return;
        }
        switch (msg.action) {
            case RESET_HAND_JINGU -> {
                ItemStack main = player.getMainHandItem();
                ItemStack off = player.getOffhandItem();
                if (main.getItem() instanceof RuyiJinguBangItem) {
                    RuyiJinguBangItem.setExtend(main, 0);
                } else if (off.getItem() instanceof RuyiJinguBangItem) {
                    RuyiJinguBangItem.setExtend(off, 0);
                }
            }
            case RESET_WORLD_JINGU -> {
                BlockEntity be = player.level().getBlockEntity(msg.pos);
                if (be instanceof JinguBangRodBlockEntity rod && player.level().getBlockState(msg.pos).getBlock()
                        instanceof JinguBangRodBlock) {
                    rod.setExtendGrid(0);
                }
            }
        }
    }
}
