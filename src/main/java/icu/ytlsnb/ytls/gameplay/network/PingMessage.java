package icu.ytlsnb.ytls.gameplay.network;

import icu.ytlsnb.ytls.framework.network.annotation.NetworkMessage;
import icu.ytlsnb.ytls.framework.network.api.NetworkContext;
import icu.ytlsnb.ytls.framework.network.api.NetworkDirection;
import icu.ytlsnb.ytls.framework.network.api.NetworkPayload;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import org.slf4j.Logger;

@NetworkMessage(id = "ping", direction = NetworkDirection.TO_SERVER)
public final class PingMessage extends NetworkPayload {
    private static final Logger LOG = FrameworkLog.of("gameplay");

    public String text = "ping";

    @Override
    public void handleServer(NetworkContext ctx) {
        ctx.enqueueWork(() -> LOG.info("Received ping from {}: {}", ctx.sender().getName().getString(), text));
    }
}
