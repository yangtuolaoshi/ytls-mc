package icu.ytlsnb.ytls.network;

import icu.ytlsnb.ytls.core.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

/**
 * Forge 1.20.4：{@link SimpleChannel} 由 {@link ChannelBuilder} 构建，不再使用 {@code NetworkRegistry.newSimpleChannel}。
 */
public final class ModNetwork {
    private static final int PROTOCOL_VERSION = 1;

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(new ResourceLocation(ModConstants.MOD_ID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .acceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .simpleChannel();

    static {
        CHANNEL.messageBuilder(OpenMiniBaguaPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenMiniBaguaPacket::decode)
                .encoder(OpenMiniBaguaPacket::encode)
                .consumerMainThread(OpenMiniBaguaPacket::handle)
                .add();

        CHANNEL.messageBuilder(BaguaActionPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .decoder(BaguaActionPacket::decode)
                .encoder(BaguaActionPacket::encode)
                .consumerMainThread(BaguaActionPacket::handle)
                .add();
    }

    private ModNetwork() {
    }

    /** 确保静态初始化执行（可在模组构造阶段调用）。 */
    public static void register() {
        // CHANNEL 静态块已完成注册
    }

    public static void sendToServer(Object msg) {
        CHANNEL.send(msg, PacketDistributor.SERVER.noArg());
    }
}
