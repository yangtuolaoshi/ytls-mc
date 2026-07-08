package icu.ytlsnb.ytls.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class CrowbarItem extends SwordItem {

  public static final float BASE_ATTACK_DAMAGE = 4.0F;
  public static final float HOMELANDER_ATTACK_DAMAGE = 20.0F;
  public static final float HOMELANDER_BONUS_DAMAGE = HOMELANDER_ATTACK_DAMAGE - BASE_ATTACK_DAMAGE;

  public CrowbarItem(Properties properties) {
    super(Tiers.WOOD, 3, -2.4F, properties);
  }
}
