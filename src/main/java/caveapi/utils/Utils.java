package caveapi.utils;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.ChunkPrimer;

public class Utils
{
    private static final int MAX_Y = 60;

    public static int findFloor(final ChunkPrimer primer, final int x, final int z) {
        for (int y = 10; y < 60; ++y) {
            if (primer.getBlockState(x, y, z).getBlock() == Blocks.AIR && primer.getBlockState(x, y - 1, z).isOpaqueCube()) {
                return y;
            }
        }
        return -1;
    }

    public static int findCeiling(final ChunkPrimer primer, final int x, final int z, final int fromY) {
        for (int y = fromY + 2; y < 80; ++y) {
            if (!primer.getBlockState(x, y, z).getBlock().isAir(primer.getBlockState(x, y, z), (IBlockAccess)null, (BlockPos)null)) {
                return y;
            }
        }
        return -1;
    }
}