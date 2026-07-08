package icu.ytlsnb.ytls.system;

import icu.ytlsnb.ytls.milk.MilkType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class MilkAbilityManager {

    public static final int ACTIVE_ABILITY_DURATION_TICKS = 20 * 10;

    private static final String TAG_DRAGON_UNTIL = "ytls_dragon_ability_until";
    private static final String TAG_WITHER_UNTIL = "ytls_wither_ability_until";
    private static final String TAG_CHICKEN_UNTIL = "ytls_chicken_ability_until";
    private static final String TAG_SPIDER_UNTIL = "ytls_spider_ability_until";
    private static final String TAG_DRAGON_CD = "ytls_dragon_ability_cd";
    private static final String TAG_WITHER_CD = "ytls_wither_ability_cd";

    private MilkAbilityManager() {
    }

    public static void grantAbility(LivingEntity livingEntity, MilkType type, int duration) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }
        long now = player.level().getGameTime();
        switch (type) {
            case DRAGON -> player.getPersistentData().putLong(TAG_DRAGON_UNTIL, now + duration);
            case WITHER -> player.getPersistentData().putLong(TAG_WITHER_UNTIL, now + duration);
            case CHICKEN -> player.getPersistentData().putLong(TAG_CHICKEN_UNTIL, now + duration);
            case SPIDER -> player.getPersistentData().putLong(TAG_SPIDER_UNTIL, now + duration);
            default -> {
            }
        }
    }

    public static void grantAbility(LivingEntity livingEntity, MilkType type) {
        grantAbility(livingEntity, type, ACTIVE_ABILITY_DURATION_TICKS);
    }

    public static boolean hasAbility(Player player, MilkType type) {
        return getRemainingTicks(player, type) > 0;
    }

    public static boolean canUse(Player player, MilkType type) {
        if (!hasAbility(player, type)) {
            return false;
        }
        long now = player.level().getGameTime();
        if (type == MilkType.DRAGON) {
            return player.getPersistentData().getLong(TAG_DRAGON_CD) <= now;
        }
        if (type == MilkType.WITHER) {
            return player.getPersistentData().getLong(TAG_WITHER_CD) <= now;
        }
        return false;
    }

    public static int getRemainingTicks(Player player, MilkType type) {
        long now = player.level().getGameTime();
        long until = switch (type) {
            case DRAGON -> player.getPersistentData().getLong(TAG_DRAGON_UNTIL);
            case WITHER -> player.getPersistentData().getLong(TAG_WITHER_UNTIL);
            case CHICKEN -> player.getPersistentData().getLong(TAG_CHICKEN_UNTIL);
            case SPIDER -> player.getPersistentData().getLong(TAG_SPIDER_UNTIL);
            default -> 0L;
        };
        return Math.max(0, (int) (until - now));
    }

    public static void consumeCooldown(Player player, MilkType type, int ticks) {
        long now = player.level().getGameTime();
        if (type == MilkType.DRAGON) {
            player.getPersistentData().putLong(TAG_DRAGON_CD, now + ticks);
        } else if (type == MilkType.WITHER) {
            player.getPersistentData().putLong(TAG_WITHER_CD, now + ticks);
        }
    }
}
