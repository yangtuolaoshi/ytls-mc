package icu.ytlsnb.ytls.client;

public final class MilkRainClientState {

    private static int activeTicks;

    private MilkRainClientState() {
    }

    public static void markActive() {
        activeTicks = 40;
    }

    public static void tick() {
        if (activeTicks > 0) {
            activeTicks--;
        }
    }

    public static boolean isActive() {
        return activeTicks > 0;
    }
}
