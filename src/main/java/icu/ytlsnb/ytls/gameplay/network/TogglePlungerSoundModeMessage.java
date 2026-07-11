package icu.ytlsnb.ytls.gameplay.network;

import icu.ytlsnb.ytls.framework.network.annotation.NetworkMessage;
import icu.ytlsnb.ytls.framework.network.api.NetworkContext;
import icu.ytlsnb.ytls.framework.network.api.NetworkDirection;
import icu.ytlsnb.ytls.framework.network.api.NetworkPayload;
import icu.ytlsnb.ytls.framework.render.PresentationEmitter;
import icu.ytlsnb.ytls.gameplay.plunger.handler.PlungerModeHelper;
import icu.ytlsnb.ytls.gameplay.plunger.item.PlungerItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * 客户端按 V 切换马桶塞「吸声音」模式。
 */
@NetworkMessage(id = "toggle_plunger_sound_mode", direction = NetworkDirection.TO_SERVER)
public final class TogglePlungerSoundModeMessage extends NetworkPayload {

    @Override
    public void handleServer(NetworkContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.sender();
            if (player == null) {
                return;
            }
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            ItemStack target = null;
            InteractionHand hand = InteractionHand.MAIN_HAND;
            if (main.getItem() instanceof PlungerItem) {
                target = main;
            } else if (off.getItem() instanceof PlungerItem) {
                target = off;
                hand = InteractionHand.OFF_HAND;
            }
            if (target == null) {
                return;
            }
            PlungerModeHelper.toggleSoundMode(target);
            boolean sound = PlungerModeHelper.isSoundMode(target);
            PresentationEmitter.actionBar(player, Component.translatable(
                    sound ? "message.ytls.plunger.sound_mode_on" : "message.ytls.plunger.sound_mode_off"));
            player.setItemInHand(hand, target);
        });
    }
}
