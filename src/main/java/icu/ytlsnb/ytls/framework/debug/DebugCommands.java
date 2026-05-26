package icu.ytlsnb.ytls.framework.debug;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.component.ComponentAccess;
import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.framework.skill.SkillCaster;
import icu.ytlsnb.ytls.framework.sync.SyncHelper;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import icu.ytlsnb.ytls.framework.worldrule.WorldRuleEngine;
import icu.ytlsnb.ytls.gameplay.component.PlayerStatsComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.Map;

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
        var dispatcher = event.getDispatcher();
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
                            if (provider == null) {
                                ctx.getSource().sendFailure(Component.literal("Registry not initialized"));
                                return 0;
                            }
                            Map<RegistryKind, Integer> counts = new EnumMap<>(RegistryKind.class);
                            for (var entry : provider.catalog()) {
                                counts.merge(entry.kind(), 1, Integer::sum);
                            }
                            StringBuilder summary = new StringBuilder("Registered entries: ")
                                    .append(provider.catalog().size());
                            counts.forEach((kind, count) ->
                                    summary.append("\n  ").append(kind).append(": ").append(count));
                            String text = summary.toString();
                            ctx.getSource().sendSuccess(() -> Component.literal(text), false);
                            return 1;
                        }))
                .then(Commands.literal("reload-config")
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "Use /forge config reload or restart world. Framework fires CONFIG_RELOAD on reload."), false);
                            return 1;
                        }))
                .then(Commands.literal("skill")
                        .then(Commands.literal("cast")
                                .then(Commands.argument("id", StringArgumentType.string())
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayer();
                                            if (player == null) {
                                                return 0;
                                            }
                                            String skillId = StringArgumentType.getString(ctx, "id");
                                            boolean ok = SkillCaster.tryCast(player, skillId);
                                            ctx.getSource().sendSuccess(() -> Component.literal(
                                                    ok ? "Skill cast started: " + skillId : "Skill cast failed: " + skillId), false);
                                            return ok ? 1 : 0;
                                        }))))
                .then(Commands.literal("rule")
                        .then(Commands.literal("toggle")
                                .then(Commands.argument("id", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String ruleId = StringArgumentType.getString(ctx, "id");
                                            boolean next = !WorldRuleEngine.isEnabled(ruleId);
                                            WorldRuleEngine.setEnabled(ruleId, next);
                                            ctx.getSource().sendSuccess(() -> Component.literal(
                                                    "Rule " + ruleId + " -> " + (next ? "enabled" : "disabled")), false);
                                            return 1;
                                        }))))
                .then(Commands.literal("component")
                        .then(Commands.literal("stats")
                                .executes(ctx -> {
                                    Player player = ctx.getSource().getPlayer();
                                    if (player == null) {
                                        return 0;
                                    }
                                    PlayerStatsComponent stats = ComponentAccess.getOrCreate(player, PlayerStatsComponent.type());
                                    stats.exampleValue++;
                                    SyncHelper.markDirty(player, PlayerStatsComponent.type());
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            "player_stats.exampleValue = " + stats.exampleValue + " (synced)"), false);
                                    return 1;
                                })));
        dispatcher.register(root);
        LOG.info("Registered debug commands under /ytls");
    }
}
