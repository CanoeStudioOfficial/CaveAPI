package com.canoestudio.caveapi.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import java.util.Random;

public class AquiferHandler {
    private static NoiseGeneratorPerlin waterNoise;
    private static long lastSeed = -1;

    public static void initialize(long seed) {
        if (seed != lastSeed) {
            waterNoise = new NoiseGeneratorPerlin(new Random(seed), 4);
            lastSeed = seed;
        }
    }

    public static void handleAquifers(ChunkPrimer primer, int chunkX, int chunkZ) {
        if (waterNoise == null) return;

        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int y = CaveAPIConfig.SEA_LEVEL - 15; y <= CaveAPIConfig.SEA_LEVEL + 10; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (primer.getBlockState(x, y, z) == CaveAPIConfig.CAVE_AIR) {
                        double noise = waterNoise.getValue(
                                (baseX + x) * CaveAPIConfig.AQUIFER_FREQUENCY,
                                y * 0.2,
                                (baseZ + z) * CaveAPIConfig.AQUIFER_FREQUENCY
                        );

                        IBlockState fluid = getFluidForDepth(y, noise);
                        if (fluid != null) {
                            primer.setBlockState(x, y, z, fluid);
                        }
                    }
                }
            }
        }
    }

    private static IBlockState getFluidForDepth(int y, double noise) {
        int depth = CaveAPIConfig.SEA_LEVEL - y;

        // 深层岩浆
        if (depth > 15 && noise < -CaveAPIConfig.LAVA_LEVEL) {
            return CaveAPIConfig.LAVA;
        }

        // 中层地下水
        if (depth > 5 && noise < 0.4) {
            return CaveAPIConfig.WATER;
        }

        // 浅层水滴
        if (depth > 0 && noise < 0.7) {
            return CaveAPIConfig.WATER;
        }

        return null;
    }
}