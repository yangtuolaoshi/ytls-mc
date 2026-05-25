package icu.ytlsnb.ytls.framework.network.api;

/**
 * 网络消息抽象基类。子类声明 public 字段，框架负责编解码与派发。
 */
public abstract class NetworkPayload {
    protected NetworkPayload() {
    }

    /**
     * 服务端收到客户端消息时的处理逻辑。
     */
    public void handleServer(NetworkContext ctx) {
    }

    /**
     * 客户端收到服务端消息时的处理逻辑。
     */
    public void handleClient(NetworkContext ctx) {
    }

    public final void dispatch(NetworkContext ctx) {
        if (ctx.isClientSide()) {
            handleClient(ctx);
        } else {
            handleServer(ctx);
        }
    }
}
