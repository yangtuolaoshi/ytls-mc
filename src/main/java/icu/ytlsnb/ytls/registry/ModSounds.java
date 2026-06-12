package icu.ytlsnb.ytls.registry;

import icu.ytlsnb.ytls.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModConstants.MOD_ID);

    public static final RegistryObject<SoundEvent> HOMELANDER_AMBIENT = register("entity.homelander.ambient");
    public static final RegistryObject<SoundEvent> HOMELANDER_HURT = register("entity.homelander.hurt");
    public static final RegistryObject<SoundEvent> HOMELANDER_DEATH = register("entity.homelander.death");
    public static final RegistryObject<SoundEvent> HOMELANDER_HEAT_VISION = register("entity.homelander.heat_vision");
    public static final RegistryObject<SoundEvent> HOMELANDER_TAME = register("entity.homelander.tame");

    private ModSounds() {
    }

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name)));
    }
}
