package caveapi.utils.worldgen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public interface CaveGenerator
{
    void generate(final World p0, final int p1, final int p2, final ChunkPrimer p3);
}
