package icu.ytlsnb.ytls.framework.render;

import cn.hutool.json.JSONUtil;
import icu.ytlsnb.ytls.framework.network.NetworkAccess;
import icu.ytlsnb.ytls.framework.render.api.PresentationType;
import icu.ytlsnb.ytls.framework.render.message.ClientPresentationMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务端统一表现触发入口，业务层不直接操作客户端 API。
 */
public final class PresentationEmitter {
    private PresentationEmitter() {
    }

    public static void particle(ServerLevel level, Vec3 pos, String particleId) {
        broadcast(level, PresentationType.PARTICLE, mapOf(
                "x", pos.x, "y", pos.y, "z", pos.z, "particle", particleId));
    }

    public static void sound(ServerPlayer player, String soundId, float volume, float pitch) {
        sendToPlayer(player, PresentationType.SOUND, mapOf(
                "sound", soundId, "volume", volume, "pitch", pitch));
    }

    public static void actionBar(Player player, Component message) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        sendToPlayer(serverPlayer, PresentationType.ACTION_BAR, mapOf("text", message.getString()));
    }

    public static void actionBar(ServerPlayer player, String text) {
        sendToPlayer(player, PresentationType.ACTION_BAR, mapOf("text", text));
    }

    private static void broadcast(ServerLevel level, PresentationType type, Map<String, Object> payload) {
        ClientPresentationMessage message = new ClientPresentationMessage();
        message.type = type;
        message.payload = JSONUtil.toJsonStr(payload);
        for (ServerPlayer player : level.players()) {
            NetworkAccess.channel().sendToPlayer(message, player);
        }
    }

    private static void sendToPlayer(ServerPlayer player, PresentationType type, Map<String, Object> payload) {
        ClientPresentationMessage message = new ClientPresentationMessage();
        message.type = type;
        message.payload = JSONUtil.toJsonStr(payload);
        NetworkAccess.channel().sendToPlayer(message, player);
    }

    private static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}
