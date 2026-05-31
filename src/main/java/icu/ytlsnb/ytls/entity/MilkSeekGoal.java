package icu.ytlsnb.ytls.entity;

import icu.ytlsnb.ytls.system.MilkWorldSystems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

public class MilkSeekGoal extends Goal {

    private final HomelanderEntity homelander;
    private final double speed;
    private BlockPos targetPos;

    public MilkSeekGoal(HomelanderEntity homelander, double speed) {
        this.homelander = homelander;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        if (homelander.getTarget() != null) {
            return false;
        }
        targetPos = MilkWorldSystems.findNearbyMilkFluid(homelander.level(), homelander.blockPosition(), 10);
        return targetPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        return targetPos != null && !homelander.getNavigation().isDone();
    }

    @Override
    public void start() {
        if (targetPos != null) {
            homelander.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), speed);
        }
    }
}
