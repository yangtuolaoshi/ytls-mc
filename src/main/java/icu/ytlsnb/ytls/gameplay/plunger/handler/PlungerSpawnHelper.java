package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 在目标方块附近找可站立的空位，避免实体生成在方块内部窒息。
 */
public final class PlungerSpawnHelper {
    private static final Direction[] NEARBY = {
            Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN
    };

    private PlungerSpawnHelper() {
    }

    public static BlockPos findNearbySpace(Level level, BlockPos origin) {
        for (Direction dir : NEARBY) {
            BlockPos candidate = origin.relative(dir);
            if (isSpawnable(level, candidate)) {
                return candidate;
            }
        }
        for (Direction dir : NEARBY) {
            BlockPos candidate = origin.relative(dir, 2);
            if (isSpawnable(level, candidate)) {
                return candidate;
            }
        }
        BlockPos above = origin.above();
        return isSpawnable(level, above) ? above : origin;
    }

    public static Vec3 centerOf(BlockPos pos) {
        return Vec3.atBottomCenterOf(pos).add(0.0D, 0.01D, 0.0D);
    }

    private static boolean isSpawnable(Level level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
    }
}
