
package caveapi.world.gen;

import caveapi.world.CaveGenerationEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class AquiferGenerator {
    /**
     * 含水层生成器，在洞穴生成后向洞穴底部添加水体
     */
    // 含水层生成的概率（0.0 ~ 1.0）
    private static final float AQUIFER_CHANCE = 0.7F;
    // 最大含水层高度（Y坐标）
    private static final int MAX_AQUIFER_LEVEL = 40;
    // 最小含水层高度（Y坐标）
    private static final int MIN_AQUIFER_LEVEL = 5;

    @SubscribeEvent
    public void onPostCaveGeneration(CaveGenerationEvent.Post event) {
        World world = event.getWorld();
        int chunkX = event.getChunkX();
        int chunkZ = event.getChunkZ();
        Random rand = world.rand;

        // 随机决定是否在当前区块生成含水层
        if (rand.nextFloat() > AQUIFER_CHANCE) return;

        CaveGenerationEvent.ChunkAccessor chunk = event.getChunk();
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        // 遍历区块内每个XZ位置
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // 获取地表高度
                int surfaceHeight = world.getHeight(baseX + x, baseZ + z);
                boolean foundWaterCandidate = false;

                // 从洞穴底部向上搜索可填充位置
                for (int y = Math.min(surfaceHeight, MAX_AQUIFER_LEVEL); y >= MIN_AQUIFER_LEVEL; y--) {
                    BlockPos pos = new BlockPos(baseX + x, y, baseZ + z);
                    IBlockState state = world.getBlockState(pos);

                    // 跳过非空气方块
                    if (!state.getBlock().isAir(state, world, pos)) continue;

                    BlockPos below = pos.down();
                    IBlockState belowState = world.getBlockState(below);

                    // 检查条件：当前为空气，下方是固体方块或水源，且在水位线以下
                    if ((isSolid(belowState) || belowState.getMaterial() == Material.WATER) &&
                            y < MAX_AQUIFER_LEVEL) {

                        // 防止在已有水体的位置重复放置
                        if (!foundWaterCandidate) {
                            // 80%概率生成水源，20%生成流动水
                            IBlockState water = (rand.nextFloat() < 0.8f) ?
                                    Blocks.WATER.getDefaultState() :
                                    Blocks.FLOWING_WATER.getDefaultState();
                            chunk.setBlockState(new BlockPos(x, y, z), water);
                            foundWaterCandidate = true;

                            // 在含水层上方添加滴水粒子效果
                            addDripParticles(world, pos.up());
                        }
                    }
                }
            }
        }
    }

    private void addDripParticles(World world, BlockPos pos) {
        // 在实际实现中，这里会添加滴水粒子效果
        // world.spawnParticle(EnumParticleTypes.DRIP_WATER, ...);
    }

    // 判断方块是否为固体（石头、泥土等）
    private boolean isSolid(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.STONE ||
                block == Blocks.DIRT ||
                block == Blocks.GRAVEL ||
                block == Blocks.SAND ||
                block == Blocks.CLAY;
    }
}