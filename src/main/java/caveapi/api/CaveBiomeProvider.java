package caveapi.api;

import net.minecraft.world.biome.Biome;
import net.minecraft.init.Biomes;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaveBiomeProvider {
    private static final List<Biome> CAVE_BIOMES = new ArrayList<>();
    private final NoiseGeneratorPerlin noise;
    private final long seed;

    public CaveBiomeProvider(long seed) {
        this.seed = seed;
        // stable_39 的正确构造方式
        this.noise = new NoiseGeneratorPerlin(new Random(seed), 4);
    }

    public static void addCaveBiome(Biome biome) {
        if (!CAVE_BIOMES.contains(biome)) {
            CAVE_BIOMES.add(biome);
        }
    }

    public Biome getBiomeAt(int x, int z) {
        if (CAVE_BIOMES.isEmpty()) {
            return Biomes.PLAINS;
        }

        // stable_39 的噪声获取方法
        double value = noise.getValue(x * 0.1, z * 0.1);
        int index = (int)(Math.abs(value) * CAVE_BIOMES.size()) % CAVE_BIOMES.size();
        return CAVE_BIOMES.get(index);
    }
}