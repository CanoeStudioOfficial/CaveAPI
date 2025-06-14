package caveapi.api;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class CaveBiomeAPI {
    private static CaveBiomeProvider caveBiomeProvider;

    public static void initializeCaveBiomes(long seed) {
        caveBiomeProvider = new CaveBiomeProvider(seed);
    }

    public static Biome injectCaveBiomes(Biome surfaceBiome, int x, int y, int z) {
        // 仅在地下生成洞穴生物群系 (y=0-32)
        if (y <= 32) {
            Biome caveBiome = caveBiomeProvider.getBiomeAt(x, z);
            return caveBiome != null ? caveBiome : surfaceBiome;
        }
        return surfaceBiome;
    }

    public static void addCaveBiome(Biome biome) {
        if (biome == null) {
            throw new IllegalArgumentException("CaveBiomeAPI: Biome cannot be null");
        }
        if (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.UNDERGROUND)) {
            BiomeDictionary.addTypes(biome, BiomeDictionary.Type.UNDERGROUND);
        }
        CaveBiomeProvider.addCaveBiome(biome);
    }
}