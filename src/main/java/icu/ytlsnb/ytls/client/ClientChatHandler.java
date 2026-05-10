package icu.ytlsnb.ytls.client;

import icu.ytlsnb.ytls.core.ModConstants;
import icu.ytlsnb.ytls.integration.ai.AiChatClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientChatHandler {
    private static final String TRIGGER = "@ai ";

    private ClientChatHandler() {
    }

    @SubscribeEvent
    public static void onChat(ClientChatEvent event) {
        String msg = event.getMessage();
        if (!msg.startsWith(TRIGGER)) {
            return;
        }
        String prompt = msg.substring(TRIGGER.length());
        Minecraft mc = Minecraft.getInstance();
        CompletableFuture.runAsync(() -> {
            String reply = AiChatClient.call(prompt);
            mc.execute(() ->
                    mc.gui.getChat().addMessage(Component.literal("[AI助手] " + reply))
            );
        });
    }
}
