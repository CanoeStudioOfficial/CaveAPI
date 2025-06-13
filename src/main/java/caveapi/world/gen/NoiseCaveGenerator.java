package caveapi.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class NoiseCaveGenerator {
    // 基于1.17的噪声算法简化实现
    public static void generate(World world, Biome biome, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        Random rand = new Random(seed);

        // 1.17+ 风格的噪声参数
        int octaves = 3 + rand.nextInt(2);
        float scale = 0.05F + rand.nextFloat() * 0.03F;
        float threshold = 0.35F + rand.nextFloat() * 0.1F;
        int waterLevel = 63;

        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        // 遍历区块内每个位置
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 5; y < world.getHeight(); y++) {
                    BlockPos pos = new BlockPos(baseX + x, y, baseZ + z);
                    IBlockState state = world.getBlockState(pos);

                    // 跳过非固体方块
                    if (!isSolid(state)) continue;

                    // 计算噪声值
                    double noise = fractalNoise(
                            baseX + x, y, baseZ + z,
                            seed, octaves, scale
                    );

                    // 根据噪声值生成洞穴
                    if (noise > threshold) {
                        world.setBlockToAir(pos);

                        // 在水位线以下生成水
                        if (y < waterLevel && rand.nextFloat() < 0.7f) {
                            world.setBlockState(pos.down(), Blocks.WATER.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    private static double fractalNoise(int x, int y, int z, long seed, int octaves, float scale) {
        double value = 0.0;
        double amplitude = 1.0;
        double frequency = scale;

        for (int i = 0; i < octaves; i++) {
            value += amplitude * simplexNoise(x * frequency, y * frequency, z * frequency, seed + i);
            amplitude *= 0.5;
            frequency *= 2.0;
        }

        return MathHelper.clamp(value, -1.0, 1.0);
    }

    private static double simplexNoise(double x, double y, double z, long seed) {
        // 使用改进的1.17+噪声算法
        return (Math.sin(x * 0.1 + seed) * 0.5 +
                Math.cos(z * 0.1 + seed) * 0.3 +
                Math.sin(y * 0.2 + seed) * 0.2);
    }

    private static boolean isSolid(IBlockState state) {
        return state.getMaterial().isSolid();
    }

}