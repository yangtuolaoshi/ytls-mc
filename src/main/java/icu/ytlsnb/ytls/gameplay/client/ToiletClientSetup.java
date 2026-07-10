package icu.ytlsnb.ytls.gameplay.client;

import icu.ytlsnb.ytls.framework.bootstrap.LifecyclePhase;
import icu.ytlsnb.ytls.framework.bootstrap.annotation.OnLifecycle;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.gameplay.plunger.entity.PlungerHookEntity;
import icu.ytlsnb.ytls.gameplay.toilet.entity.ToiletSeatEntity;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;

public final class ToiletClientSetup {
    private ToiletClientSetup() {
    }

    @OnLifecycle(LifecyclePhase.CLIENT_SETUP)
    public static void onClientSetup() {
        EntityRenderers.register(
                (net.minecraft.world.entity.EntityType<ToiletSeatEntity>) RegistryAccess.resolve(RegistryKind.ENTITY, "toilet_seat"),
                NoopRenderer::new
        );
        EntityRenderers.register(
                (net.minecraft.world.entity.EntityType<PlungerHookEntity>) RegistryAccess.resolve(RegistryKind.ENTITY, "plunger_hook"),
                PlungerHookRenderer::new
        );
    }
}
