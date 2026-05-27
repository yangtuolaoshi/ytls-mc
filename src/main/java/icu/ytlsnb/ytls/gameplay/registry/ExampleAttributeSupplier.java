package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterAttribute;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

@RegisterAttribute("example_power")
public final class ExampleAttributeSupplier implements RegistrySupplier<Attribute> {
    @Override
    public Attribute get() {
        return new RangedAttribute(
                ModResources.loc("example_power").toString(),
                1.0D,
                0.0D,
                10.0D
        ).setSyncable(true);
    }
}
