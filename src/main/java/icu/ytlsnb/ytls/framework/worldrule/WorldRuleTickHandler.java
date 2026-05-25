package icu.ytlsnb.ytls.framework.worldrule;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.worldrule.api.RuleContext;
import icu.ytlsnb.ytls.framework.worldrule.api.WorldRule;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public final class WorldRuleTickHandler {
    private WorldRuleTickHandler() {
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.level instanceof ServerLevel serverLevel)) {
            return;
        }
        WorldRuleEngine.tickAll(new WorldRuleEngine.RuleContextFactory() {
            @Override
            public ServerLevel level() {
                return serverLevel;
            }

            @Override
            public RuleContext contextFor(WorldRule rule) {
                return new RuleContext(serverLevel);
            }
        });
    }
}
