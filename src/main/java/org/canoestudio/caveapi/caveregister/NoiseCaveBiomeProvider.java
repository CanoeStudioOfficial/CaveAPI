package org.canoestudio.caveapi.caveregister;

import net.minecraft.world.World;
import org.canoestudio.caveapi.api.CaveBiome;
import org.canoestudio.caveapi.api.ICaveBiomeProvider;
import org.canoestudio.caveapi.nosie.OpenSimplex2S;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Noise-based 3D Cave Biome Provider
 * Uses 3D noise to smoothly distribute biomes in space
 */
public class NoiseCaveBiomeProvider implements ICaveBiomeProvider {
    private long seed;
    private final double scale;
    private final double yCompression;

    /**
     * Create a new noise-based provider
     * @param scale Horizontal/vertical scale of biome areas
     * @param yCompression Compression factor for vertical distribution (higher means biomes change faster vertically)
     */
    public NoiseCaveBiomeProvider(double scale, double yCompression) {
        this.scale = scale;
        this.yCompression = yCompression;
    }

    @Override
    public void initialize(long seed) {
        this.seed = seed;
    }

    @Override
    public CaveBiome getBiome(World world, int x, int y, int z) {
        // Use 3D noise to get a value between -1 and 1
        float noiseValue = OpenSimplex2S.noise3_ImproveXZ(seed, x / scale, (y / scale) * yCompression, z / scale);
        
        // Normalize noise value to 0..1
        double normalizedValue = (noiseValue + 1.0) / 2.0;

        // Get biomes available at this height
        Map<Integer, CaveBiome> weightedBiomes = new TreeMap<>();
        int totalWeight = 0;

        for (CaveBiome biome : CaveBiomeRegistry.getAllBiomes().values()) {
            if (y >= biome.getMinY() && y <= biome.getMaxY()) {
                totalWeight += biome.getWeight();
                weightedBiomes.put(totalWeight, biome);
            }
        }

        if (totalWeight == 0) {
            // Fallback to the first biome if none match height
            List<CaveBiome> allBiomes = new ArrayList<>(CaveBiomeRegistry.getAllBiomes().values());
            return allBiomes.isEmpty() ? null : allBiomes.get(0);
        }

        // Use the noise value to pick a biome based on cumulative weights
        double targetWeight = normalizedValue * totalWeight;
        
        for (Map.Entry<Integer, CaveBiome> entry : weightedBiomes.entrySet()) {
            if (targetWeight <= entry.getKey()) {
                return entry.getValue();
            }
        }

        return weightedBiomes.values().iterator().next();
    }
}
