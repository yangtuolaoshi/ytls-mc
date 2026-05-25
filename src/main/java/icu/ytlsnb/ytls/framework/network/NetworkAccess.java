package icu.ytlsnb.ytls.framework.network;

/**
 * 业务层访问网络通道的静态入口。
 */
public final class NetworkAccess {
    private static NetworkChannel channel;

    private NetworkAccess() {
    }

    public static void bind(NetworkChannel networkChannel) {
        channel = networkChannel;
    }

    public static NetworkChannel channel() {
        if (channel == null) {
            throw new IllegalStateException("Network channel not initialized");
        }
        return channel;
    }
}
