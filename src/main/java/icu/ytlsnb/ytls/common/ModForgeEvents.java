package icu.ytlsnb.ytls.common;

import icu.ytlsnb.ytls.core.ModConstants;
import icu.ytlsnb.ytls.item.RuyiJinguBangItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModForgeEvents {
    private static final UUID RUYI_BLOCK_REACH_UUID = UUID.fromString("a7e2c4d1-9b3f-4d8e-b2c1-0f1a2b3c4d5e");
    private static final UUID RUYI_ENTITY_REACH_UUID = UUID.fromString("b8f3d5e2-0c4a-5e9f-c3d2-1a2b3c4d5e6f");

    private ModForgeEvents() {
    }

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        if (event.getSlotType() != EquipmentSlot.MAINHAND) {
            return;
        }
        if (!(event.getItemStack().getItem() instanceof RuyiJinguBangItem)) {
            return;
        }
        int ext = RuyiJinguBangItem.getExtend(event.getItemStack());
        if (ext <= 0) {
            return;
        }
        event.addModifier(
                ForgeMod.BLOCK_REACH.get(),
                new AttributeModifier(RUYI_BLOCK_REACH_UUID, "ytls:ruyi_block_reach", ext, AttributeModifier.Operation.ADDITION)
        );
        event.addModifier(
                ForgeMod.ENTITY_REACH.get(),
                new AttributeModifier(RUYI_ENTITY_REACH_UUID, "ytls:ruyi_entity_reach", ext, AttributeModifier.Operation.ADDITION)
        );
    }
}
