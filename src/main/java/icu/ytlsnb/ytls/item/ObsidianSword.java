package icu.ytlsnb.ytls.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;

public class ObsidianSword extends SwordItem {
    public ObsidianSword() {
        /*
         * 参数1：等级
         * 参数2：伤害
         * 参数3：攻击速度
         * 参数4：物品基本属性
         */
        super(ModItemTier.OBSIDIAN, 5, -1.0F, new Item.Properties());
    }
}
