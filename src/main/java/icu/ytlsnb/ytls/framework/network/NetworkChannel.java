package icu.ytlsnb.ytls.framework.network;

import icu.ytlsnb.ytls.framework.network.annotation.NetworkMessage;
import icu.ytlsnb.ytls.framework.network.api.NetworkPayload;
import icu.ytlsnb.ytls.framework.util.ClasspathScanner;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Forge 1.20.4 SimpleChannel 实现，自动注册带 {@link NetworkMessage} 注解的消息类。
 */
public final class NetworkChannel {
    private static final Logger LOG = FrameworkLog.network();

    private final SimpleChannel channel;
    private final Map<String, Class<? extends NetworkPayload>> classById = new HashMap<>();

    public NetworkChannel() {
        this.channel = ChannelBuilder
                .named(ModResources.loc("main"))
                .networkProtocolVersion(1)
                .simpleChannel();
    }

    public void scanAndRegister(String basePackage) {
        for (Class<?> clazz : ClasspathScanner.scanByAnnotation(basePackage, NetworkMessage.class)) {
            if (!NetworkPayload.class.isAssignableFrom(clazz)) {
                throw new IllegalStateException(
                        "@NetworkMessage class must extend NetworkPayload: " + clazz.getName());
            }
            @SuppressWarnings("unchecked")
            Class<? extends NetworkPayload> payloadClass = (Class<? extends NetworkPayload>) clazz;
            NetworkMessage meta = clazz.getAnnotation(NetworkMessage.class);
            registerPayload(meta.id(), meta.direction(), payloadClass);
        }
    }

    private void registerPayload(
            String messageId,
            icu.ytlsnb.ytls.framework.network.api.NetworkDirection direction,
            Class<? extends NetworkPayload> clazz
    ) {
        if (classById.containsKey(messageId)) {
            throw new IllegalStateException("Duplicate network message id: " + messageId);
        }
        classById.put(messageId, clazz);

        Supplier<NetworkPayload> factory = () -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new IllegalStateException(
                        "Network payload requires public no-arg constructor: " + clazz.getName(), ex);
            }
        };

        if (direction == icu.ytlsnb.ytls.framework.network.api.NetworkDirection.TO_SERVER
                || direction == icu.ytlsnb.ytls.framework.network.api.NetworkDirection.BIDIRECTIONAL) {
            registerDirectionTyped(clazz, factory, net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER);
        }
        if (direction == icu.ytlsnb.ytls.framework.network.api.NetworkDirection.TO_CLIENT
                || direction == icu.ytlsnb.ytls.framework.network.api.NetworkDirection.BIDIRECTIONAL) {
            registerDirectionTyped(clazz, factory, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
        }

        FrameworkLog.networkDebug("Registered network message '{}' -> {} ({})", messageId, clazz.getSimpleName(), direction);
    }

    @SuppressWarnings("unchecked")
    private <T extends NetworkPayload> void registerDirectionTyped(
            Class<? extends NetworkPayload> clazz,
            Supplier<NetworkPayload> factory,
            net.minecraftforge.network.NetworkDirection forgeDirection
    ) {
        Class<T> typedClass = (Class<T>) clazz;
        channel.messageBuilder(typedClass, forgeDirection)
                .encoder((payload, buf) -> PayloadCodec.writePayload(payload, buf))
                .decoder(buf -> {
                    T payload = typedClass.cast(factory.get());
                    PayloadCodec.readPayload(payload, buf);
                    return payload;
                })
                .consumerMainThread((payload, ctx) -> handle(payload, ctx))
                .add();
    }

    private void handle(NetworkPayload payload, CustomPayloadEvent.Context ctx) {
        ForgeNetworkContext networkContext = new ForgeNetworkContext(ctx);
        payload.dispatch(networkContext);
    }

    public void sendToServer(NetworkPayload payload) {
        channel.send(payload, PacketDistributor.SERVER.noArg());
    }

    public void sendToPlayer(NetworkPayload payload, ServerPlayer player) {
        channel.send(payload, PacketDistributor.PLAYER.with(player));
    }

    public void sendToAllPlayers(NetworkPayload payload) {
        channel.send(payload, PacketDistributor.ALL.noArg());
    }

    /**
     * 向指定玩家发送（用于实体同步等场景）。
     */
    public void sendToPlayers(NetworkPayload payload, Iterable<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            sendToPlayer(payload, player);
        }
    }
}
