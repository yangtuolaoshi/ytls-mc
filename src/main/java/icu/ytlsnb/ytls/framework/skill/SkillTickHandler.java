package icu.ytlsnb.ytls.framework.skill;

import icu.ytlsnb.ytls.ModConstants;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public final class SkillTickHandler {
    private SkillTickHandler() {
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide() || living.isRemoved()) {
            return;
        }
        SkillCaster.tick(living);
    }
}
