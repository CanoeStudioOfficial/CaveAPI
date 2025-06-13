package caveapi.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;
import java.util.function.Predicate;

public class FeatureUtils {
    public static boolean testAdjacentStates(World world, BlockPos pos, Predicate<IBlockState> predicate) {
        for (EnumFacing direction : EnumFacing.values()) {
            BlockPos adjacent = pos.offset(direction);
            if (predicate.test(world.getBlockState(adjacent))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExposedToAir(World world, BlockPos pos) {
        return testAdjacentStates(world, pos, state -> state.getBlock().isAir(state, world, pos));
    }
}