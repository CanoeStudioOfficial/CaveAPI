package caveapi.compat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;

// 1.12.2 区块基类适配
public class ChunkPrimer {
    private final BlockStateContainer data;
    private final World world;

    public ChunkPrimer(World world, BlockStateContainer data) {
        this.world = world;
        this.data = data;
    }

    public World getWorld() {
        return world;
    }

    public IBlockState getBlockState(int x, int y, int z) {
        return data.get(x, y, z);
    }

    public void setBlockState(int x, int y, int z, IBlockState state) {
        data.set(x, y, z, state);
    }
}