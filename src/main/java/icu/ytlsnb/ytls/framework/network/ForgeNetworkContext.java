package icu.ytlsnb.ytls.framework.network;

import icu.ytlsnb.ytls.framework.network.api.NetworkContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public final class ForgeNetworkContext implements NetworkContext {
    private final CustomPayloadEvent.Context context;

    public ForgeNetworkContext(CustomPayloadEvent.Context context) {
        this.context = context;
    }

    @Override
    public boolean isClientSide() {
        return context.isClientSide();
    }

    @Override
    public ServerPlayer sender() {
        return context.getSender();
    }

    @Override
    public void enqueueWork(Runnable work) {
        context.enqueueWork(work);
    }

    @Override
    public void setHandled() {
        context.setPacketHandled(true);
    }
}
