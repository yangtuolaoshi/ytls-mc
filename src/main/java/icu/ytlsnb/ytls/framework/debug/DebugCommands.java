package icu.ytlsnb.ytls.framework.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * 框架调试命令，仅在开发环境或显式开启调试时注册。
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public final class DebugCommands {
    private static final Logger LOG = FrameworkLog.of("debug");

    private DebugCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (!DevEnvironment.isDebugActive()) {
            return;
        }
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("ytls")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("debug")
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.literal("YTLS debug mode active"), false);
                            return 1;
                        }))
                .then(Commands.literal("registry")
                        .executes(ctx -> {
                            ForgeRegistryProvider provider = RegistryAccess.provider();
                            int count = provider == null ? 0 : provider.catalog().size();
                            ctx.getSource().sendSuccess(() -> Component.literal("Registered entries: " + count), false);
                            return 1;
                        }))
                .then(Commands.literal("reload-config")
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "Config reload requested. Restart world or use /forge config reload if available."), false);
                            return 1;
                        }));
        dispatcher.register(root);
        LOG.info("Registered debug commands under /ytls");
    }
}
