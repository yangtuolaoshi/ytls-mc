package icu.ytlsnb.ytls.gameplay.toilet.item;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

@RegisterItem("toilet")
public class ToiletBlockItem extends BlockItem {
    public ToiletBlockItem() {
        super(RegistryAccess.block("toilet"), new Item.Properties());
    }
}
