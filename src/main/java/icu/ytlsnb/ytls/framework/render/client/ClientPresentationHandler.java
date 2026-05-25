package icu.ytlsnb.ytls.framework.render.client;

import cn.hutool.json.JSONUtil;
import icu.ytlsnb.ytls.framework.render.api.PresentationType;
import icu.ytlsnb.ytls.framework.render.message.ClientPresentationMessage;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.Map;

/**
 * 客户端表现处理器，仅处理 {@link ClientPresentationMessage}。
 */
public final class ClientPresentationHandler {
    private static final Logger LOG = FrameworkLog.of("presentation");

    private ClientPresentationHandler() {
    }

    public static void handle(ClientPresentationMessage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> data = JSONUtil.toBean(message.payload, Map.class);
        switch (message.type) {
            case PARTICLE -> spawnParticle(mc, data);
            case SOUND -> playSound(mc, data);
            case ACTION_BAR -> showActionBar(mc, data);
            case BOSS_BAR_HINT -> LOG.debug("Boss bar hint: {}", data);
        }
    }

    private static void spawnParticle(Minecraft mc, Map<String, Object> data) {
        double x = toDouble(data.get("x"));
        double y = toDouble(data.get("y"));
        double z = toDouble(data.get("z"));
        mc.level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0.1, 0);
    }

    private static void playSound(Minecraft mc, Map<String, Object> data) {
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        String soundKey = String.valueOf(data.getOrDefault("sound", "minecraft:entity.experience_orb.pickup"));
        ResourceLocation id = parseResourceLocation(soundKey);
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(id);
        if (sound == null) {
            LOG.warn("Sound not found, skipped: {}", id);
            return;
        }
        float volume = (float) toDouble(data.getOrDefault("volume", 1.0D));
        float pitch = (float) toDouble(data.getOrDefault("pitch", 1.0D));
        player.playSound(sound, volume, pitch);
    }

    private static void showActionBar(Minecraft mc, Map<String, Object> data) {
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        player.displayClientMessage(Component.literal(String.valueOf(data.getOrDefault("text", ""))), true);
    }

    private static double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private static ResourceLocation parseResourceLocation(String key) {
        int split = key.indexOf(':');
        if (split <= 0) {
            return new ResourceLocation("minecraft", key);
        }
        return new ResourceLocation(key.substring(0, split), key.substring(split + 1));
    }
}
