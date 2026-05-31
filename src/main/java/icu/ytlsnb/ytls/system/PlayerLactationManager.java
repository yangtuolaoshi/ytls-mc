package icu.ytlsnb.ytls.system;

import net.minecraft.world.entity.player.Player;

public final class PlayerLactationManager {

    private static final String TAG_LACTATION = "ytls_lactation_ml";
    public static final int MAX_LACTATION = 3000;

    private PlayerLactationManager() {
    }

    public static int getLactation(Player player) {
        return player.getPersistentData().getInt(TAG_LACTATION);
    }

    public static void setLactation(Player player, int value) {
        player.getPersistentData().putInt(TAG_LACTATION, Math.max(0, Math.min(MAX_LACTATION, value)));
    }

    public static void addLactation(Player player, int delta) {
        setLactation(player, getLactation(player) + delta);
    }

    public static boolean consumeForBucket(Player player) {
        int value = getLactation(player);
        if (value < 1000) {
            return false;
        }
        setLactation(player, value - 1000);
        return true;
    }
}
