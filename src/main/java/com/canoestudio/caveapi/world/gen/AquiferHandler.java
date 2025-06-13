package com.canoestudio.caveapi.world.gen;


import com.canoestudio.caveapi.core.CaveAPIConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import java.util.Random;

public class AquiferHandler {
    private static NoiseGeneratorPerlin waterNoise;
    private static long lastSeed = -1;

    public static void initialize(long seed) {
        if (seed != lastSeed) {
            refresh(seed);
            lastSeed = seed;
        }
    }

    public static void refresh(long seed) {
        waterNoise = new NoiseGeneratorPerlin(new Random(seed), 4);
    }

    public static boolean shouldHandleAquifers() {
        return waterNoise != null &&
                CaveAPIConfig.aquiferFrequency > 0.001f &&
                CaveAPIConfig.seaLevel > 0;
    }

    public static void handleAquifers(ChunkPrimer primer, int chunkX, int chunkZ) {
        if (!shouldHandleAquifers()) return;

        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int y = CaveAPIConfig.seaLevel - 15; y <= CaveAPIConfig.seaLevel + 10; y++) {
            if (y < 0 || y >= 256) continue;

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (primer.getBlockState(x, y, z) == CaveAPIConfig.getCaveAirState()) {
                        double noise = waterNoise.getValue(
                                (baseX + x) * CaveAPIConfig.aquiferFrequency,
                                y * 0.2,
                                (baseZ + z) * CaveAPIConfig.aquiferFrequency
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
        int depth = CaveAPIConfig.seaLevel - y;

        // 跳过地表附近的含水层
        if (depth < 5) return null;

        // 深层岩浆
        if (depth > 30 && noise < -CaveAPIConfig.lavaLevel) {
            return CaveAPIConfig.getLavaState();
        }

        // 中层地下水
        if (depth > 15 && noise < 0.4) {
            return CaveAPIConfig.getWaterState();
        }

        // 浅层水滴
        if (depth > 5 && noise < 0.7) {
            return CaveAPIConfig.getWaterState();
        }

        return null;
    }
}