package icu.ytlsnb.ytls.client;

import icu.ytlsnb.ytls.block.JinguBangRodBlock;
import icu.ytlsnb.ytls.core.ModConstants;
import icu.ytlsnb.ytls.item.MiniBaguaFurnaceItem;
import icu.ytlsnb.ytls.item.RuyiJinguBangItem;
import icu.ytlsnb.ytls.network.BaguaActionPacket;
import icu.ytlsnb.ytls.network.ModNetwork;
import icu.ytlsnb.ytls.network.OpenMiniBaguaPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientBaguaInput {
    private static boolean baguaKeyWasDown;

    private ClientBaguaInput() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }
        boolean down = ModKeyBindings.BAGUA_ACTION.isDown();
        boolean pressed = down && !baguaKeyWasDown;
        baguaKeyWasDown = down;
        if (!pressed) {
            return;
        }
        var player = mc.player;
        assert player != null;
        var main = player.getMainHandItem();
        var off = player.getOffhandItem();

        if (main.getItem() instanceof MiniBaguaFurnaceItem) {
            ModNetwork.sendToServer(new OpenMiniBaguaPacket(player.getInventory().selected));
            return;
        }
        if (off.getItem() instanceof MiniBaguaFurnaceItem) {
            ModNetwork.sendToServer(new OpenMiniBaguaPacket(Inventory.SLOT_OFFHAND));
            return;
        }

        HitResult hit = mc.hitResult;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            if (mc.level != null && mc.level.getBlockState(pos).getBlock() instanceof JinguBangRodBlock) {
                ModNetwork.sendToServer(new BaguaActionPacket(BaguaActionPacket.Action.RESET_WORLD_JINGU, pos));
                return;
            }
        }

        if (main.getItem() instanceof RuyiJinguBangItem || off.getItem() instanceof RuyiJinguBangItem) {
            ModNetwork.sendToServer(new BaguaActionPacket(BaguaActionPacket.Action.RESET_HAND_JINGU, BlockPos.ZERO));
        }
    }
}
