package icu.ytlsnb.ytls.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import icu.ytlsnb.ytls.milk.MilkType;
import icu.ytlsnb.ytls.system.MilkRainManager;
import icu.ytlsnb.ytls.system.MilkWorldSystems;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;

public final class ModCommands {

    private ModCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("milkrain")
            .then(Commands.argument("milkType", StringArgumentType.word())
                .then(Commands.argument("seconds", IntegerArgumentType.integer(5, 600))
                    .executes(context -> {
                        String milkName = StringArgumentType.getString(context, "milkType");
                        MilkType type = findByName(milkName);
                        if (type == null) {
                            context.getSource().sendFailure(Component.literal("未知奶类型: " + milkName));
                            return 0;
                        }
                        int seconds = IntegerArgumentType.getInteger(context, "seconds");
                        return MilkRainManager.forceStart(context.getSource(), type, seconds);
                    }))));
        event.getDispatcher().register(Commands.literal("ytls_use_ability")
            .executes(context -> {
                if (context.getSource().getPlayer() == null) {
                    return 0;
                }
                return MilkWorldSystems.tryUseActiveAbility(context.getSource().getPlayer()) ? 1 : 0;
            }));
        event.getDispatcher().register(Commands.literal("ytls_collect_self_milk")
            .executes(context -> {
                if (context.getSource().getPlayer() == null) {
                    return 0;
                }
                return MilkWorldSystems.tryCollectSelfMilk(context.getSource().getPlayer()) ? 1 : 0;
            }));
        event.getDispatcher().register(Commands.literal("ytls_lay_egg")
            .executes(context -> {
                if (context.getSource().getPlayer() == null) {
                    return 0;
                }
                return MilkWorldSystems.tryLayEgg(context.getSource().getPlayer()) ? 1 : 0;
            }));
    }

    private static MilkType findByName(String value) {
        for (MilkType type : MilkType.values()) {
            if (type.name().equalsIgnoreCase(value) || type.id().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
