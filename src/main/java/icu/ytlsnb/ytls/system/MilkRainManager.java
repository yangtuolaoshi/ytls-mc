package icu.ytlsnb.ytls.system;

import icu.ytlsnb.ytls.milk.MilkType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class MilkRainManager {

    private static final Map<ResourceKeyLike, RainState> STATES = new HashMap<>();

    private MilkRainManager() {
    }

    public static void beginTick(ServerLevel level) {
        getState(level).altarTypes.clear();
    }

    public static void reportAltarMilk(Level level, MilkType type) {
        if (level instanceof ServerLevel serverLevel) {
            getState(serverLevel).altarTypes.add(type);
        }
    }

    public static void endTick(ServerLevel level) {
        RainState state = getState(level);
        long gameTime = level.getGameTime();
        if (gameTime >= state.nextRandomCheck) {
            state.nextRandomCheck = gameTime + 200;
            if (!state.active && level.random.nextInt(1200) == 0) {
                state.active = true;
                state.until = gameTime + 20L * 90L;
                state.types.clear();
                if (!state.altarTypes.isEmpty()) {
                    state.types.addAll(state.altarTypes);
                }
            } else if (state.active && gameTime > state.until) {
                state.active = false;
                state.types.clear();
            }
        }
        if (state.active) {
            if (!state.altarTypes.isEmpty()) {
                state.types.addAll(state.altarTypes);
            }
            MilkWorldSystems.applyMilkRainEffects(level, state.types);
        }
    }

    public static Set<MilkType> getActiveTypes(ServerLevel level) {
        return getState(level).types;
    }

    public static boolean isActive(ServerLevel level) {
        return getState(level).active;
    }

    public static int forceStart(CommandSourceStack sourceStack, MilkType type, int seconds) {
        ServerLevel level = sourceStack.getLevel();
        RainState state = getState(level);
        state.active = true;
        state.types.clear();
        state.types.add(type);
        state.until = level.getGameTime() + seconds * 20L;
        sourceStack.sendSuccess(() -> Component.literal("已在当前维度召唤 " + type.id() + " 奶雨"), true);
        return 1;
    }

    private static RainState getState(ServerLevel level) {
        return STATES.computeIfAbsent(new ResourceKeyLike(level.dimension().location().toString()), key -> new RainState());
    }

    private record ResourceKeyLike(String key) {
    }

    private static final class RainState {
        private final Set<MilkType> types = EnumSet.noneOf(MilkType.class);
        private final Set<MilkType> altarTypes = EnumSet.noneOf(MilkType.class);
        private long until = 0L;
        private long nextRandomCheck = 0L;
        private boolean active = false;
    }
}
