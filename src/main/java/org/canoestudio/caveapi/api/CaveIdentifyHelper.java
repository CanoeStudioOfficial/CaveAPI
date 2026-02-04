package org.canoestudio.caveapi.api;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.canoestudio.caveapi.block.ModBlocks;

/**
 * Cave Identification Helper
 * Provides utility methods to identify cave locations using Cave Air.
 */
public class CaveIdentifyHelper {

    /**
     * Check if the given position is a cave (marked with Cave Air)
     * @param world The world
     * @param pos The position
     * @return True if the block at the position is Cave Air
     */
    public static boolean isCave(World world, BlockPos pos) {
        if (world == null || pos == null) return false;
        IBlockState state = world.getBlockState(pos);
        return isCave(state);
    }

    /**
     * Check if the given block state is Cave Air
     * @param state The block state
     * @return True if the state is Cave Air
     */
    public static boolean isCave(IBlockState state) {
        if (state == null) return false;
        return state.getBlock() == ModBlocks.CAVE_AIR;
    }

    /**
     * Get the Cave Air block instance
     * @return The Cave Air block
     */
    public static Block getCaveAir() {
        return ModBlocks.CAVE_AIR;
    }
}
