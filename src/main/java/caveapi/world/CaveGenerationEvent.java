package caveapi.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CaveGenerationEvent extends Event {
    protected final World world;
    protected final int chunkX;
    protected final int chunkZ;

    public CaveGenerationEvent(World world, int chunkX, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    // 添加公共访问方法
    public World getWorld() {
        return world;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public static class Pre extends CaveGenerationEvent {
        public Pre(World world, int chunkX, int chunkZ) {
            super(world, chunkX, chunkZ);
        }
    }

    public static class Post extends CaveGenerationEvent {
        public Post(World world, int chunkX, int chunkZ) {
            super(world, chunkX, chunkZ);
        }

        public ChunkAccessor getChunk() {
            return new ChunkAccessor(world, chunkX, chunkZ);
        }
    }

    // 提供安全的区块修改接口
    public static class ChunkAccessor {
        private final World world;
        private final int x, z;

        public ChunkAccessor(World world, int x, int z) {
            this.world = world;
            this.x = x;
            this.z = z;
        }

        public void setBlockState(BlockPos pos, IBlockState state) {
            world.setBlockState(new BlockPos(x * 16 + pos.getX(), pos.getY(), z * 16 + pos.getZ()), state);
        }
    }
}