package icu.ytlsnb.ytls.gameplay.plunger.item;

import icu.ytlsnb.ytls.framework.registry.annotation.RegisterItem;
import icu.ytlsnb.ytls.gameplay.plunger.entity.WeatherCloudEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@RegisterItem("rain_cloud")
public class RainCloudItem extends Item {
    public RainCloudItem() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Vec3 pos = Vec3.atCenterOf(context.getClickedPos().relative(context.getClickedFace())).add(0, 1.5D, 0);
        WeatherCloudEntity cloud = new WeatherCloudEntity(level, WeatherCloudEntity.CloudKind.RAIN);
        cloud.setPos(pos.x, pos.y, pos.z);
        level.addFreshEntity(cloud);
        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
