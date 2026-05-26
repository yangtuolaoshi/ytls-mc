package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterSound;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.sounds.SoundEvent;

@RegisterSound("example_chime")
public final class ExampleSoundSupplier implements RegistrySupplier<SoundEvent> {
    @Override
    public SoundEvent get() {
        return SoundEvent.createVariableRangeEvent(ModResources.loc("example_chime"));
    }
}
