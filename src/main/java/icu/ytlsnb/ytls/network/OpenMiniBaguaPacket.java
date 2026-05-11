package icu.ytlsnb.ytls.network;

import icu.ytlsnb.ytls.item.MiniBaguaFurnaceItem;
import icu.ytlsnb.ytls.world.inventory.MiniBaguaMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

public final class OpenMiniBaguaPacket {
    private final int slot;

    public OpenMiniBaguaPacket(int slot) {
        this.slot = slot;
    }

    public static void encode(OpenMiniBaguaPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.slot);
    }

    public static OpenMiniBaguaPacket decode(FriendlyByteBuf buf) {
        return new OpenMiniBaguaPacket(buf.readVarInt());
    }

    public static void handle(OpenMiniBaguaPacket msg, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) {
            return;
        }
        int slot = msg.slot;
        if (slot < 0 || slot >= player.getInventory().getContainerSize()) {
            return;
        }
        ItemStack stack = player.getInventory().getItem(slot);
        if (!(stack.getItem() instanceof MiniBaguaFurnaceItem)) {
            return;
        }
        MenuProvider provider = new SimpleMenuProvider(
                (menuId, inv, p) -> new MiniBaguaMenu(menuId, inv, slot),
                Component.translatable("screen.ytls.mini_bagua")
        );
        player.openMenu(provider, payload -> payload.writeVarInt(slot));
    }
}
