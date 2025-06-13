package com.canoestudio.caveapi.core;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class CaveHooks {
    public static void handleCaveGeneration(World world, int chunkX, int chunkZ, ChunkPrimer primer) {
        if (CaveAPIConfig.replaceVanillaCaves) {
            CaveAPI.generateCaves(world, primer, chunkX, chunkZ);
        }
    }
}