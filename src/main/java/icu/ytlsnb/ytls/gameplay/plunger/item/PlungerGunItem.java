package icu.ytlsnb.ytls.gameplay.plunger.item;

import icu.ytlsnb.ytls.gameplay.plunger.entity.PlungerHookEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@icu.ytlsnb.ytls.framework.registry.annotation.RegisterItem("plunger_gun")
public class PlungerGunItem extends Item {
    private static final String TAG_HOOK = "HookEntity";

    public PlungerGunItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        PlungerHookEntity existing = findHook(level, stack);
        if (existing != null && existing.isAlive()) {
            if (existing.getHookMode() != PlungerHookEntity.HookMode.REELING) {
                existing.startReelIn();
            }
            playRetrieveSound(level, player);
            return InteractionResultHolder.consume(stack);
        }

        if (hasHookTag(stack)) {
            clearHook(stack);
            playRetrieveSound(level, player);
            return InteractionResultHolder.consume(stack);
        }

        PlungerHookEntity hook = new PlungerHookEntity(level, player);
        hook.shootFromPlayer(player, player.getAbilities().instabuild ? 1.0F : 0.4F);
        if (level.addFreshEntity(hook)) {
            setHook(stack, hook.getUUID());
            player.awardStat(Stats.ITEM_USED.get(this));
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.8F);
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Nullable
    public static PlungerHookEntity findHook(Level level, ItemStack stack) {
        if (!hasHookTag(stack)) {
            return null;
        }
        UUID id = stack.getTag().getUUID(TAG_HOOK);
        if (level instanceof ServerLevel serverLevel
                && serverLevel.getEntity(id) instanceof PlungerHookEntity hook) {
            return hook;
        }
        return null;
    }

    public static void clearHookForEntity(Player player, UUID hookId) {
        clearHookIfMatch(player.getMainHandItem(), hookId);
        clearHookIfMatch(player.getOffhandItem(), hookId);
    }

    private static void clearHookIfMatch(ItemStack stack, UUID hookId) {
        if (hasHookTag(stack) && stack.getTag().getUUID(TAG_HOOK).equals(hookId)) {
            clearHook(stack);
        }
    }

    private static boolean hasHookTag(ItemStack stack) {
        return stack.hasTag() && stack.getTag().hasUUID(TAG_HOOK);
    }

    private static void setHook(ItemStack stack, UUID id) {
        stack.getOrCreateTag().putUUID(TAG_HOOK, id);
    }

    private static void clearHook(ItemStack stack) {
        if (stack.hasTag()) {
            stack.getTag().remove(TAG_HOOK);
        }
    }

    private static void playRetrieveSound(Level level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }
}
