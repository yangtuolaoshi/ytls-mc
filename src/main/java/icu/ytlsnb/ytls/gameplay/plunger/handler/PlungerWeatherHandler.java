package icu.ytlsnb.ytls.gameplay.plunger.handler;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 仰头 ≥60° 对空右键：吸走雨/雷暴，给予对应云物品。
 */
public final class PlungerWeatherHandler {
    /** Minecraft 抬头为负俯仰角，-60 表示抬头 60° */
    public static final float SKY_PITCH_THRESHOLD = -60.0F;

    private PlungerWeatherHandler() {
    }

    public static boolean isLookingAtSky(Player player) {
        return player.getXRot() <= SKY_PITCH_THRESHOLD;
    }

    public static boolean trySuckWeather(Level level, Player player, ItemStack plunger) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        if (!isLookingAtSky(player)) {
            return false;
        }
        if (PlungerModeHelper.isSoundMode(plunger)) {
            return false;
        }
        if (!serverLevel.isRaining() && !serverLevel.isThundering()) {
            return false;
        }
        if (!PlungerBlockHandler.damagePlunger(plunger, player)) {
            return false;
        }

        boolean thunder = serverLevel.isThundering();
        // 变晴：清除雨与雷暴
        serverLevel.setWeatherParameters(6000, 0, false, false);

        ItemStack cloud = new ItemStack(RegistryAccess.item(thunder ? "thunder_cloud" : "rain_cloud"));
        if (!player.getInventory().add(cloud)) {
            PlungerBlockHandler.spawnItem(serverLevel, player.blockPosition(), cloud);
        }
        return true;
    }
}
