package com.canoestudio.caveapi.event;


import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public abstract class CaveGenerationEvent extends Event {
    protected final World world;
    protected final ChunkPrimer primer;
    protected final int chunkX;
    protected final int chunkZ;

    public CaveGenerationEvent(World world, ChunkPrimer primer, int chunkX, int chunkZ) {
        this.world = world;
        this.primer = primer;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public World getWorld() {
        return world;
    }

    public ChunkPrimer getPrimer() {
        return primer;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public static class Pre extends CaveGenerationEvent {
        public Pre(World world, ChunkPrimer primer, int chunkX, int chunkZ) {
            super(world, primer, chunkX, chunkZ);
        }
    }

    public static class Post extends CaveGenerationEvent {
        public Post(World world, ChunkPrimer primer, int chunkX, int chunkZ) {
            super(world, primer, chunkX, chunkZ);
        }
    }
}