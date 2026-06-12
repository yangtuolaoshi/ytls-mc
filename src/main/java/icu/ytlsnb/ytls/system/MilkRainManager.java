package icu.ytlsnb.ytls.system;

import icu.ytlsnb.ytls.milk.MilkType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class MilkRainManager {

    public static final String MILK_RAIN_SCOREBOARD = "ytls_milk_rain";
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
            suppressVanillaRain(level, state);
            if (!state.altarTypes.isEmpty()) {
                state.types.addAll(state.altarTypes);
            }
            MilkWorldSystems.applyMilkRainEffects(level, state.types);
        } else if (state.forcedWeather) {
            restoreVanillaRain(level, state);
        }
        syncMilkRainFlag(level, state.active);
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

    private static void suppressVanillaRain(ServerLevel level, RainState state) {
        if (!state.forcedWeather || level.isRaining()) {
            // 奶雨的视觉由自定义奶滴粒子负责，关闭原版蓝色雨幕。
            level.setWeatherParameters(20 * 120, 0, false, false);
            state.forcedWeather = true;
        }
    }

    private static void restoreVanillaRain(ServerLevel level, RainState state) {
        level.setWeatherParameters(20 * 120, 0, false, false);
        state.forcedWeather = false;
    }

    private static void syncMilkRainFlag(ServerLevel level, boolean active) {
        Scoreboard scoreboard = level.getScoreboard();
        Objective objective = scoreboard.getObjective(MILK_RAIN_SCOREBOARD);
        if (objective == null) {
            objective = scoreboard.addObjective(
                MILK_RAIN_SCOREBOARD,
                ObjectiveCriteria.DUMMY,
                Component.literal("YTLS Milk Rain"),
                ObjectiveCriteria.RenderType.INTEGER,
                false,
                null
            );
        }
        int value = active ? 1 : 0;
        for (ServerPlayer player : level.players()) {
            scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()), objective).set(value);
        }
    }

    private record ResourceKeyLike(String key) {
    }

    private static final class RainState {
        private final Set<MilkType> types = EnumSet.noneOf(MilkType.class);
        private final Set<MilkType> altarTypes = EnumSet.noneOf(MilkType.class);
        private long until = 0L;
        private long nextRandomCheck = 0L;
        private boolean active = false;
        private boolean forcedWeather = false;
    }
}
