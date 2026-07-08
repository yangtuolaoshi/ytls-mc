package icu.ytlsnb.ytls.event;

import icu.ytlsnb.ytls.entity.HomelanderEntity;
import icu.ytlsnb.ytls.item.CrowbarItem;
import icu.ytlsnb.ytls.milk.MilkType;
import icu.ytlsnb.ytls.registry.ModEntityTypes;
import icu.ytlsnb.ytls.registry.ModFluids;
import icu.ytlsnb.ytls.registry.ModItems;
import icu.ytlsnb.ytls.system.MilkRainManager;
import icu.ytlsnb.ytls.system.MilkWorldSystems;
import icu.ytlsnb.ytls.system.PlayerLactationManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod.EventBusSubscriber(modid = icu.ytlsnb.ytls.ModConstants.MOD_ID)
public final class ModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ModEvents() {
    }

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.HOMELANDER.get(), HomelanderEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onEntityMilk(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack item = event.getItemStack();
        Entity target = event.getTarget();

        if (!item.is(Items.BUCKET)) {
            return;
        }
        MilkType type = MilkWorldSystems.findMilkForEntity(target);
        if (type == null || !MilkWorldSystems.canProduceMilk(target)) {
            return;
        }

        if (!player.level().isClientSide) {
            item.shrink(1);
            type.resolveBucketItem().ifPresent(bucket -> player.addItem(new ItemStack(bucket)));
            player.swing(event.getHand(), true);
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onVanillaMilkPlace(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!event.getItemStack().is(Items.MILK_BUCKET)) {
            return;
        }
        Level level = player.level();
        if (level.isClientSide) {
            return;
        }
        var pos = event.getPos().relative(event.getFace());
        if (!level.getBlockState(pos).canBeReplaced()) {
            return;
        }
        level.setBlock(pos, ModFluids.SOURCE_FLUIDS.get(MilkType.COW).get().defaultFluidState().createLegacyBlock(), 11);
        if (!player.getAbilities().instabuild) {
            event.getItemStack().shrink(1);
            player.addItem(new ItemStack(Items.BUCKET));
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onDrinkMilk(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem();
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player player)) {
            return;
        }
        if (MilkWorldSystems.isAnyMilkBucket(stack)) {
            MilkWorldSystems.aggroHomelanderForDrinking(player.level(), player);
        }
        if (stack.is(ModItems.GALACTAGOGUE.get())) {
            PlayerLactationManager.addLactation(player, 500);
        }
    }

    @SubscribeEvent
    public static void onCrowbarHomelanderDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof HomelanderEntity)) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        if (!event.getSource().is(DamageTypes.PLAYER_ATTACK)) {
            return;
        }
        if (!player.getMainHandItem().is(ModItems.CROWBAR.get())) {
            return;
        }
        event.setAmount(event.getAmount() + CrowbarItem.HOMELANDER_BONUS_DAMAGE);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide || living.tickCount % 10 != 0) {
            return;
        }
        FluidState fluidState = living.level().getFluidState(living.blockPosition());
        if (!fluidState.isEmpty() && MilkWorldSystems.isMilkFluid(fluidState.getType())) {
            MilkWorldSystems.applyFluidEffects((ServerLevel) living.level(), living, fluidState.getType());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase == TickEvent.Phase.START) {
            MilkWorldSystems.applySpiderClimbDuringTravel(player);
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (player.tickCount % 100 == 0) {
            PlayerLactationManager.addLactation(player, 1);
        }
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (player.tickCount % 200 == 0) {
                LOGGER.info("[YTLS] player={} lactation={}ml", serverPlayer.getGameProfile().getName(), PlayerLactationManager.getLactation(player));
            }
            if (PlayerLactationManager.getLactation(player) >= PlayerLactationManager.MAX_LACTATION && player.tickCount % 8 == 0) {
                MilkWorldSystems.spawnMilkOverflowParticles((ServerLevel) player.level(), player);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (!(event.level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            MilkRainManager.beginTick(serverLevel);
            MilkWorldSystems.triggerCreeperMilkFluidExplosions(serverLevel);
            if (serverLevel.getGameTime() % 100L == 0L) {
                for (Player player : serverLevel.players()) {
                    if (MilkWorldSystems.findNearbyMilkFluid(serverLevel, player.blockPosition(), 8) != null && serverLevel.random.nextInt(3) == 0) {
                        MilkWorldSystems.spawnHomelanderNear(serverLevel, player.blockPosition(), 1);
                    }
                }
            }
        } else {
            MilkRainManager.endTick(serverLevel);
        }
    }
}
