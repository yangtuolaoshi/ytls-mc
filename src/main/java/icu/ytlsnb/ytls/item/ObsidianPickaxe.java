package icu.ytlsnb.ytls.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;

public class ObsidianPickaxe extends PickaxeItem {
    public ObsidianPickaxe() {
        /*
         * 参数1：物品等级
         * 参数2：基础攻击伤害
         * 参数3：攻击速度
         * 参数4：属性
         */
        super(ModItemTier.OBSIDIAN, 2, -2.4F, new Item.Properties());
    }
}
