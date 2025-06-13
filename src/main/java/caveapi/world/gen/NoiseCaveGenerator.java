package caveapi.world.gen;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.util.math.MathHelper;

public class NoiseCaveGenerator {
    // 基于1.17的噪声算法简化实现
    public static void generate(World world, int chunkX, int chunkZ, int octaves, float scale) {
        ChunkPrimer primer = new ChunkPrimer();
        long seed = world.getSeed();

        double[][][] noiseMap = new double[16][256][16];
        double frequency = scale;

        // 生成3D噪声
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    noiseMap[x][y][z] = fractalNoise(
                            chunkX * 16 + x, y, chunkZ * 16 + z,
                            seed, octaves, frequency
                    );
                }
            }
        }

        // 应用阈值创建洞穴形状
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    if (noiseMap[x][y][z] > 0.4) {
                        primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    private static double fractalNoise(int x, int y, int z, long seed, int octaves, float frequency) {
        double value = 0.0;
        double amplitude = 1.0;

        for (int i = 0; i < octaves; i++) {
            value += amplitude * simplexNoise(x * frequency, y * frequency, z * frequency, seed + i);
            amplitude *= 0.5;
            frequency *= 2.0;
        }

        return MathHelper.clamp(value, -1.0, 1.0);
    }

    // 简化版Simplex噪声实现
    private static double simplexNoise(double x, double y, double z, long seed) {
        // 实际实现需使用完整3D Simplex噪声算法
        return Math.sin(x * 0.1 + seed) + Math.cos(z * 0.1 + seed);
    }
}