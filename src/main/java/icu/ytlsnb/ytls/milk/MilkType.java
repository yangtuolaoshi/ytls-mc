package icu.ytlsnb.ytls.milk;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public enum MilkType {
    COW("cow_milk", "ytls:cow_milk_bucket", 80, 40, 0, 0xFFF5F2E8),
    CHICKEN("chicken_milk", "ytls:chicken_milk_bucket", 160, 100, 0, 0xFFFFF1C9),
    CREEPER("creeper_milk", "ytls:creeper_milk_bucket", 100, 0, 0, 0xFFBFE8C2),
    ENDERMAN("enderman_milk", "ytls:enderman_milk_bucket", 100, 0, 20, 0xFF7B3FA6),
    VILLAGER("villager_milk", "ytls:villager_milk_bucket", 160, 100, 0, 0xFFD8C7A1),
    RABBIT("rabbit_milk", "ytls:rabbit_milk_bucket", 160, 100, 0, 0xFFF7DDE6),
    SHULKER("shulker_milk", "ytls:shulker_milk_bucket", 100, 60, 0, 0xFF5B4D9A),
    SPIDER("spider_milk", "ytls:spider_milk_bucket", 100, 80, 0, 0xFF4A3A2A),
    DRAGON("dragon_milk", "ytls:dragon_milk_bucket", 100, 0, 0, 0xFF3A1A4D),
    WITHER("wither_milk", "ytls:wither_milk_bucket", 100, 0, 0, 0xFF2B0F1F),
    PLAYER("player_milk", "ytls:player_milk_bucket", 100, 0, 0, 0xFFF5F2E8);

    private final String id;
    private final String bucketItemId;
    private final int drinkDurationTicks;
    private final int fluidEffectDurationTicks;
    private final int rainCooldownTicks;
    private final int fluidTintColor;

    MilkType(String id, String bucketItemId, int drinkDurationTicks, int fluidEffectDurationTicks, int rainCooldownTicks, int fluidTintColor) {
        this.id = id;
        this.bucketItemId = bucketItemId;
        this.drinkDurationTicks = drinkDurationTicks;
        this.fluidEffectDurationTicks = fluidEffectDurationTicks;
        this.rainCooldownTicks = rainCooldownTicks;
        this.fluidTintColor = fluidTintColor;
    }

    public String id() {
        return id;
    }

    public String bucketItemId() {
        return bucketItemId;
    }

    public int drinkDurationTicks() {
        return drinkDurationTicks;
    }

    public int fluidEffectDurationTicks() {
        return fluidEffectDurationTicks;
    }

    public int rainCooldownTicks() {
        return rainCooldownTicks;
    }

    public int fluidTintColor() {
        return fluidTintColor;
    }

    public Component displayName() {
        return Component.translatable("milk_type.ytls." + id);
    }

    public Optional<Item> resolveBucketItem() {
        return Optional.ofNullable(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(bucketItemId)));
    }

    public void applyStandardEffect(LivingEntity entity, int durationTicks) {
        switch (this) {
            case COW -> {
                if (entity instanceof Player player) {
                    player.removeAllEffects();
                } else {
                    entity.removeAllEffects();
                }
            }
            case CHICKEN -> entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, durationTicks, 0, false, true));
            case VILLAGER -> entity.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, durationTicks, 0, false, true));
            case RABBIT -> entity.addEffect(new MobEffectInstance(MobEffects.JUMP, durationTicks, 1, false, true));
            case SHULKER -> entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, durationTicks, 0, false, true));
            case SPIDER -> entity.addEffect(new MobEffectInstance(MobEffects.POISON, durationTicks, 0, false, true));
            default -> {
            }
        }
    }

    public boolean needsActiveAbility() {
        return this == DRAGON || this == WITHER || this == CHICKEN || this == SPIDER;
    }

    public static void playInstantEffect(Level level, LivingEntity entity, MilkType type) {
        if (type == CREEPER && !level.isClientSide) {
            level.explode(null, entity.getX(), entity.getY(), entity.getZ(), 2.7F, Level.ExplosionInteraction.NONE);
        } else if (type == ENDERMAN && !level.isClientSide) {
            entity.randomTeleport(entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 32.0D,
                entity.getY() + (entity.getRandom().nextDouble() - 0.5D) * 16.0D,
                entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * 32.0D,
                true);
        }
    }

    public static MilkType fromFluidName(String path) {
        for (MilkType type : values()) {
            if (path.contains(type.id)) {
                return type;
            }
        }
        return COW;
    }
}
