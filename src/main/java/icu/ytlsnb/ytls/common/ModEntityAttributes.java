package icu.ytlsnb.ytls.common;

import icu.ytlsnb.ytls.core.ModConstants;
import icu.ytlsnb.ytls.entity.AIWolfEntity;
import icu.ytlsnb.ytls.init.ModEntityTypes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEntityAttributes {
    private ModEntityAttributes() {
    }

    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.AI_WOLF.get(), AIWolfEntity.createAttributes().build());
    }
}
