package org.canoestudio.caveapi.compat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Central registry for block remappers.
 * Provides a unified way to handle cross-mod block replacements.
 */
public class UniversalCompatHandler {
    private static final List<IBlockRemapper> REMAPPERS = new ArrayList<>();

    /**
     * Register a new remapper.
     */
    public static void registerRemapper(IBlockRemapper remapper) {
        if (!REMAPPERS.contains(remapper)) {
            REMAPPERS.add(remapper);
        }
    }

    /**
     * Applies all registered remappers to the given block state.
     */
    public static IBlockState applyRemappers(World world, BlockPos pos, IBlockState currentState, int type) {
        IBlockState result = currentState;
        for (IBlockRemapper remapper : REMAPPERS) {
            result = remapper.remap(world, pos, result, type);
        }
        return result;
    }
}
