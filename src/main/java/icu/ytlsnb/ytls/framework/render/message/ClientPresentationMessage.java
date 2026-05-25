package icu.ytlsnb.ytls.framework.render.message;

import icu.ytlsnb.ytls.framework.network.annotation.NetworkMessage;
import icu.ytlsnb.ytls.framework.network.annotation.NetworkMessage;
import icu.ytlsnb.ytls.framework.network.api.NetworkContext;
import icu.ytlsnb.ytls.framework.network.api.NetworkDirection;
import icu.ytlsnb.ytls.framework.network.api.NetworkPayload;
import icu.ytlsnb.ytls.framework.render.api.PresentationType;
import icu.ytlsnb.ytls.framework.render.client.ClientPresentationHandler;

@NetworkMessage(id = "client_presentation", direction = NetworkDirection.TO_CLIENT)
public final class ClientPresentationMessage extends NetworkPayload {
    public PresentationType type = PresentationType.PARTICLE;
    public String payload = "";

    @Override
    public void handleClient(NetworkContext ctx) {
        ctx.enqueueWork(() -> ClientPresentationHandler.handle(this));
    }
}
