package com.personthecat.cavegenerator.world.data;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.concurrent.ConcurrentHashMap;

public class ChunkStaging {

    //private ConcurrentHashMap<ChunkPos, Integer> staging = new ConcurrentHashMap<>();
    //private static ConcurrentHashMap<World, ChunkStaging> worldChunkStagingMap = new ConcurrentHashMap<>();
    //public World world;

    public static void setChunkStaging(World world, int x, int z, int stage) {
        getChunkCap(world, x, z).getData().setByte("s", (byte)stage);
        getChunk(world, x, z).markDirty();
    }

    public static int getChunkStaging(World world, int x, int z) {
        WorldData data = getChunkCap(world, x, z);
        return (int)data.getData().getByte("s");
    }


    private static WorldData getWorldCap(World world) {
        return world.getCapability(WorldDataHandler.CG_WORLD_CAP, null);
    }

    private static WorldData getChunkCap(World world, int x, int z) {
        return getChunk(world, x, z).getCapability(WorldDataHandler.CG_WORLD_CAP, null);
    }

    private static Chunk getChunk(World world, int x, int z) {
        return world.getChunk(x, z);
    }

}
