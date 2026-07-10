package icu.ytlsnb.ytls.gameplay.common;

import icu.ytlsnb.ytls.framework.bootstrap.LifecyclePhase;
import icu.ytlsnb.ytls.framework.bootstrap.annotation.OnLifecycle;
import icu.ytlsnb.ytls.framework.event.annotation.Listen;
import icu.ytlsnb.ytls.framework.event.api.GameEventType;
import icu.ytlsnb.ytls.framework.event.events.PlayerLogoutEvent;
import icu.ytlsnb.ytls.framework.event.events.PlayerTickEvent;
import icu.ytlsnb.ytls.gameplay.plunger.handler.PlungerSuctionHandler;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.gameplay.toilet.block.ToiletBlock;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

public final class ToiletGameplaySetup {
    private ToiletGameplaySetup() {
    }

    @Listen(GameEventType.PLAYER_TICK)
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.player().level().isClientSide) {
            return;
        }
        PlungerSuctionHandler.tickPlayer(event.player());
    }

    @Listen(GameEventType.PLAYER_LOGOUT)
    public static void onPlayerLogout(PlayerLogoutEvent event) {
        PlungerSuctionHandler.clearPlayer(event.player().getUUID());
    }

    @OnLifecycle(LifecyclePhase.DATA_GEN)
    public static void onDataGen(GatherDataEvent event) {
        var output = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(
                event.includeServer(),
                new icu.ytlsnb.ytls.gameplay.data.ToiletRecipeProvider(output)
        );
        event.getGenerator().addProvider(
                event.includeClient(),
                new ToiletBlockStateProvider(output, event.getExistingFileHelper())
        );
    }

    private static final class ToiletBlockStateProvider extends BlockStateProvider {
        ToiletBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
            super(output, icu.ytlsnb.ytls.ModConstants.MOD_ID, helper);
        }

        @Override
        protected void registerStatesAndModels() {
            var toilet = RegistryAccess.block("toilet");
            getVariantBuilder(toilet).forAllStates(state -> {
                int count = state.getValue(ToiletBlock.POOP_COUNT);
                String model = "toilet_" + count;
                return ConfiguredModel.builder()
                        .modelFile(models().getExistingFile(modLoc("block/" + model)))
                        .build();
            });
        }
    }
}
