package icu.ytlsnb.ytls.gameplay.plunger.handler;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

/**
 * 苦力怕爆炸前 1 秒内可吸走爆炸；之后永久无法再爆炸、也不再蓄力。
 * <p>
 * 自然靠近蓄力靠 {@link SwellGoal} 推 swell，打火石点燃靠 {@code isIgnited()}；
 * 两者都只要 swell 进入爆炸前约 1 秒窗口即可吸取。
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlungerCreeperHandler {
    public static final String TAG_DISARMED = "ExplosionSucked";
    /** swell=10 / (maxSwell-2)=28 ≈ 爆炸前约 1 秒 */
    private static final float SWELL_WINDOW = 10.0F / 28.0F;

    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED;
    private static final Field SWELL_FIELD;
    private static final Field OLD_SWELL_FIELD;

    static {
        try {
            // findField 需要 SRG 名：开发环境会映射到 Mojang 名，正式环境直接使用
            // DATA_IS_IGNITED / swell / oldSwell -> f_32275_ / f_32270_ / f_32269_ (1.20.4)
            Field ignited = ObfuscationReflectionHelper.findField(Creeper.class, "f_32275_");
            @SuppressWarnings("unchecked")
            EntityDataAccessor<Boolean> accessor = (EntityDataAccessor<Boolean>) ignited.get(null);
            DATA_IS_IGNITED = accessor;

            SWELL_FIELD = ObfuscationReflectionHelper.findField(Creeper.class, "f_32270_");
            OLD_SWELL_FIELD = ObfuscationReflectionHelper.findField(Creeper.class, "f_32269_");
        } catch (ReflectiveOperationException | RuntimeException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private PlungerCreeperHandler() {
    }

    public static boolean isDisarmed(Creeper creeper) {
        return creeper.getPersistentData().getBoolean(TAG_DISARMED);
    }

    public static void disarm(Creeper creeper) {
        CompoundTag data = creeper.getPersistentData();
        data.putBoolean(TAG_DISARMED, true);
        extinguishFuse(creeper);
        disableSwellGoal(creeper);
    }

    /** 熄灭点燃并清零 swell，避免仍走 explodeCreeper → discard。 */
    private static void extinguishFuse(Creeper creeper) {
        creeper.getEntityData().set(DATA_IS_IGNITED, false);
        creeper.setSwellDir(-1);
        try {
            SWELL_FIELD.setInt(creeper, 0);
            OLD_SWELL_FIELD.setInt(creeper, 0);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to reset creeper swell", e);
        }
    }

    /** 移除自然蓄力 AI，缴械后不再对玩家 swell。 */
    private static void disableSwellGoal(Creeper creeper) {
        creeper.goalSelector.removeAllGoals(goal -> goal instanceof SwellGoal);
    }

    public static boolean isInAbsorbWindow(Creeper creeper) {
        if (isDisarmed(creeper) || creeper.isRemoved()) {
            return false;
        }
        // 自然蓄力：swellDir>0；打火石：isIgnited。二者都看 swell 进度。
        boolean charging = creeper.getSwellDir() > 0 || creeper.isIgnited();
        if (!charging) {
            return false;
        }
        return creeper.getSwelling(0.0F) >= SWELL_WINDOW;
    }

    public static boolean trySuckExplosion(Player player, ItemStack plunger, Creeper creeper) {
        if (PlungerModeHelper.isSoundMode(plunger)) {
            return false;
        }
        if (!isInAbsorbWindow(creeper)) {
            return false;
        }
        if (!PlungerBlockHandler.damagePlunger(plunger, player)) {
            return false;
        }
        disarm(creeper);
        ItemStack boom = new ItemStack(RegistryAccess.item("sucked_explosion"));
        if (!player.getInventory().add(boom)) {
            PlungerBlockHandler.spawnItem(player.level(), creeper.blockPosition(), boom);
        }
        return true;
    }

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Start event) {
        if (event.getExplosion().getDirectSourceEntity() instanceof Creeper creeper && isDisarmed(creeper)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onCreeperTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Creeper creeper) || creeper.level().isClientSide) {
            return;
        }
        if (creeper.isRemoved() || !isDisarmed(creeper)) {
            return;
        }
        // 读档后 SwellGoal 会重新注册，每 tick 确保卸掉并熄灭 fuse
        disableSwellGoal(creeper);
        if (creeper.isIgnited() || creeper.getSwelling(0.0F) > 0.0F || creeper.getSwellDir() > 0) {
            extinguishFuse(creeper);
        }
    }
}
