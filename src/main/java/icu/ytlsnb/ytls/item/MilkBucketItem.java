package icu.ytlsnb.ytls.item;

import icu.ytlsnb.ytls.milk.MilkType;
import icu.ytlsnb.ytls.system.MilkAbilityManager;
import icu.ytlsnb.ytls.system.PlayerLactationManager;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class MilkBucketItem extends BucketItem {

    private final MilkType milkType;

    public MilkBucketItem(MilkType milkType, Fluid fluid, Properties properties) {
        super(fluid, properties);
        this.milkType = milkType;
    }

    public MilkType milkType() {
        return milkType;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (milkType.needsActiveAbility()) {
            MilkAbilityManager.grantAbility(entity, milkType);
        }

        if (!level.isClientSide) {
            MilkType.playInstantEffect(level, entity, milkType);
            milkType.applyStandardEffect(entity, milkType.drinkDurationTicks());
            if (milkType == MilkType.PLAYER && entity instanceof Player player) {
                PlayerLactationManager.addLactation(player, 200);
            }
            if (milkType == MilkType.CREEPER) {
                for (int i = 0; i < 20; i++) {
                    level.addParticle(ParticleTypes.EXPLOSION, entity.getX(), entity.getEyeY(), entity.getZ(), 0, 0, 0);
                }
            }
        }

        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                return new ItemStack(Items.BUCKET);
            }
            player.getInventory().add(new ItemStack(Items.BUCKET));
        }

        return stack;
    }
}
