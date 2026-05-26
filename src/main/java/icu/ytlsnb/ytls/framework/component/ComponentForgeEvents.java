package icu.ytlsnb.ytls.framework.component;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public final class ComponentForgeEvents {
    private static final Logger LOG = FrameworkLog.of("component");
    private static final ResourceLocation CAP_ID = ComponentAccess.capabilityId();

    private ComponentForgeEvents() {
    }

    @SubscribeEvent
    public static void attachEntity(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(CAP_ID, new ComponentCapabilities.Provider());
    }

    @SubscribeEvent
    public static void attachBlockEntity(AttachCapabilitiesEvent<BlockEntity> event) {
        event.addCapability(CAP_ID, new ComponentCapabilities.Provider());
    }

    @SubscribeEvent
    public static void attachLevel(AttachCapabilitiesEvent<Level> event) {
        event.addCapability(CAP_ID, new ComponentCapabilities.Provider());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player clone = event.getEntity();
        original.getCapability(ComponentCapabilities.HOLDER).ifPresent(oldHolder ->
                clone.getCapability(ComponentCapabilities.HOLDER).ifPresent(newHolder ->
                        newHolder.copyFrom(oldHolder)));
        LOG.debug("Copied player components on clone for {}", clone.getName().getString());
    }
}
