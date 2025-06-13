package com.canoestudio.caveapi.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import java.util.Random;

public class CaveBiomeProvider {
    private static NoiseGeneratorPerlin biomeNoise;
    private static long lastSeed = -1;

    public static void init(long seed) {
        if (seed != lastSeed) {
            biomeNoise = new NoiseGeneratorPerlin(new Random(seed), 2);
            lastSeed = seed;
        }
    }

    public static Biome getCaveBiome(int x, int y, int z, Biome surfaceBiome) {
        if (biomeNoise == null) return surfaceBiome;

        double noise = biomeNoise.getValue(x * 0.1, z * 0.1) * 0.5 + 0.5;

        // 深层生物群系（-64 ~ -32）
        if (y < 32) {
            return noise > 0.7 ? Biome.getBiome(8) : Biome.getBiome(40); // 深暗之域/滴水石锥
        }

        // 中层生物群系（-32 ~ 32）
        if (y < 64) {
            if (surfaceBiome.getBiomeName().contains("Desert")) {
                return Biome.getBiome(2); // 沙岩洞穴
            }
            return noise > 0.6 ? Biome.getBiome(1) : surfaceBiome; // 普通洞穴
        }

        // 浅层生物群系（32+）
        return surfaceBiome;
    }
}