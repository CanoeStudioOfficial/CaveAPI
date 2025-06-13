package com.canoestudio.caveapi.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public interface ICaveGenerator {
    void generate(World world, ChunkPrimer primer, int chunkX, int chunkZ);
}