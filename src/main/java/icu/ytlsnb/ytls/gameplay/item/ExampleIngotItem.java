package icu.ytlsnb.ytls.gameplay.item;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterItem;
import net.minecraft.world.item.Item;

@RegisterItem("example_ingot")
public class ExampleIngotItem extends Item {
    public ExampleIngotItem() {
        super(new Item.Properties());
    }
}
