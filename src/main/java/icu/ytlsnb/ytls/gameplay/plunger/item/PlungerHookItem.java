package icu.ytlsnb.ytls.gameplay.plunger.item;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterItem;
import net.minecraft.world.item.Item;

/**
 * 仅用于马桶塞枪飞行钩的渲染外观，不进入创造栏，无游戏内用途。
 */
@RegisterItem("plunger_hook")
public class PlungerHookItem extends Item {
    public PlungerHookItem() {
        super(new Item.Properties());
    }
}
