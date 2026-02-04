package org.canoestudio.caveapi.compat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Universal interface for remapping blocks during cave generation.
 * This allows multiple mods to influence the final block state.
 */
public interface IBlockRemapper {
    /**
     * @param world The world being generated
     * @param pos The position of the block
     * @param currentState The current block state intended for this position
     * @param type 0: wall, 1: floor, 2: ceiling
     * @return The replacement block state, or currentState if no change is needed
     */
    IBlockState remap(World world, BlockPos pos, IBlockState currentState, int type);
}
