package icu.ytlsnb.ytls.framework.network.api;

import net.minecraft.server.level.ServerPlayer;

/**
 * 网络消息处理上下文，业务层通过此接口回复或获取发送方。
 */
public interface NetworkContext {
    boolean isClientSide();

    ServerPlayer sender();

    void enqueueWork(Runnable work);

    void setHandled();
}
