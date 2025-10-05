package org.caveapi.world.generator;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.caveapi.data.LayerSettings;
import org.caveapi.world.LayersBiomeArray;

import java.util.HashMap;
import java.util.List;

public class LayerGenerator extends BasicGenerator {

    private final LayerSettings cfg;

    List<LayersBiomeArray> layersBiomeArraysList;
    final HashMap<Integer, Biome> layersBiomeArraysHashMap = new HashMap<>();
    final HashMap<Integer, Biome> layersBiomeArraysHashMap2 = new HashMap<>();
    final HashMap<Integer, Biome> layersBiomeArraysHashMap3 = new HashMap<>();
    final HashMap<Integer, Biome> layersBiomeArraysHashMap4 = new HashMap<>();

    public LayerGenerator(LayerSettings cfg, World world) {
        super(cfg.conditions, world);
        this.cfg = cfg;
    }

    @Override
    public void generate(PrimerContext ctx) {
        final int dim = ctx.world.provider.getDimension();
        if (conditions.dimensions.test(dim)) {
            generateChecked(ctx);
        }
    }

    /** Check biomes at the local X/Z coordinate in the chunk **/
    public boolean checkCoordinate(int cX, int cZ) {
        if (this.conditions.hasBiomes) {
            int biome = layersBiomeArraysList.get(conditions.proxyDimension).biomeArray[cX][cZ];
            if (!this.conditions.biomes.test(layersBiomeArraysHashMap.get(biome))) {
                return false;
            }
        }
        if (this.conditions.hasBiomes2) {
            int biome = layersBiomeArraysList.get(conditions.proxyDimension2).biomeArray[cX][cZ];
            if (!this.conditions.biomes2.test(layersBiomeArraysHashMap2.get(biome))) {
                return false;
            }
        }
        if (this.conditions.hasBiomes3) {
            int biome = layersBiomeArraysList.get(conditions.proxyDimension3).biomeArray[cX][cZ];
            if (!this.conditions.biomes3.test(layersBiomeArraysHashMap3.get(biome))) {
                return false;
            }
        }
        if (this.conditions.hasBiomes4) {
            int biome = layersBiomeArraysList.get(conditions.proxyDimension4).biomeArray[cX][cZ];
            if (!this.conditions.biomes4.test(layersBiomeArraysHashMap4.get(biome))) {
                return false;
            }
        }
        return true;
    }

    /** Check biomes for if this layer should try generating here **/
    public boolean checkFull(PrimerContext ctx) {
        final int dim = ctx.world.provider.getDimension();
        if (!conditions.dimensions.test(dim)) {
            return false;
        }
        List<Integer> biomes;
        if (this.conditions.hasBiomes) {
            biomes = layersBiomeArraysList.get(conditions.proxyDimension).layerBiomes;
            boolean stopGen = true;
            for(int biome : biomes) {
                if (this.conditions.biomes.test(Biome.getBiomeForId(biome))) {
                    stopGen = false;
                }
            }
            if (stopGen)
                return false;
        }
        if (this.conditions.hasBiomes2) {
            biomes = layersBiomeArraysList.get(conditions.proxyDimension2).layerBiomes;
            boolean stopGen = true;
            for(int biome : biomes) {
                if (this.conditions.biomes2.test(Biome.getBiomeForId(biome))) {
                    stopGen = false;
                }
            }
            if (stopGen)
                return false;
        }
        if (this.conditions.hasBiomes3) {
            biomes = layersBiomeArraysList.get(conditions.proxyDimension3).layerBiomes;
            boolean stopGen = true;
            for(int biome : biomes) {
                if (this.conditions.biomes3.test(Biome.getBiomeForId(biome))) {
                    stopGen = false;
                }
            }
            if (stopGen)
                return false;
        }
        if (this.conditions.hasBiomes4) {
            biomes = layersBiomeArraysList.get(conditions.proxyDimension4).layerBiomes;
            boolean stopGen = true;
            for(int biome : biomes) {
                if (this.conditions.biomes4.test(Biome.getBiomeForId(biome))) {
                    stopGen = false;
                }
            }
            if (stopGen)
                return false;
        }
        return true;
    }

    public void generate(PrimerContext ctx, List<LayersBiomeArray> layers) {
        this.layersBiomeArraysList = layers;
        if (this.checkFull(ctx)) {
            List<Integer> biomes;
            if (this.conditions.hasBiomes) {
                biomes = layersBiomeArraysList.get(conditions.proxyDimension).layerBiomes;
                for(int biome : biomes) {
                    if (!layersBiomeArraysHashMap.containsKey(biome))
                        this.layersBiomeArraysHashMap.put(biome, Biome.getBiomeForId(biome));
                }
            }
            if (this.conditions.hasBiomes2) {
                biomes = layersBiomeArraysList.get(conditions.proxyDimension2).layerBiomes;
                for(int biome : biomes) {
                    if (!layersBiomeArraysHashMap2.containsKey(biome))
                        this.layersBiomeArraysHashMap2.put(biome, Biome.getBiomeForId(biome));
                }
            }
            if (this.conditions.hasBiomes3) {
                biomes = layersBiomeArraysList.get(conditions.proxyDimension3).layerBiomes;
                for(int biome : biomes) {
                    if (!layersBiomeArraysHashMap3.containsKey(biome))
                        this.layersBiomeArraysHashMap3.put(biome, Biome.getBiomeForId(biome));
                }
            }
            if (this.conditions.hasBiomes4) {
                biomes = layersBiomeArraysList.get(conditions.proxyDimension4).layerBiomes;
                for(int biome : biomes) {
                    if (!layersBiomeArraysHashMap4.containsKey(biome))
                        this.layersBiomeArraysHashMap4.put(biome, Biome.getBiomeForId(biome));
                }
            }
            this.generateChecked(ctx);
        }
    }

    @Override
    protected void generateChecked(PrimerContext ctx) {
        if (conditions.hasRegion) {
            for (int x = 0; x < 16; x++) {
                final int actualX = x + (ctx.chunkX * 16);
                for (int z = 0; z < 16; z++) {
                    final int actualZ = z + (ctx.chunkZ * 16);
                    if (checkCoordinate(x, z) && conditions.region.GetBoolean(actualX, actualZ)) {
                        for (int y : conditions.getColumn(actualX, actualZ)) {
                            if (cfg.matchers.contains(ctx.primer.getBlockState(x, y, z))) {
                                if (conditions.noise.GetBoolean(x, z)) {
                                    ctx.primer.setBlockState(x, y, z, cfg.state);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = 0; x < 16; x++) {
                final int actualX = x + (ctx.chunkX * 16);
                for (int z = 0; z < 16; z++) {
                    final int actualZ = z + (ctx.chunkZ * 16);
                    if (checkCoordinate(x, z)) {
                        for (int y : conditions.getColumn(actualX, actualZ)) {
                            if (cfg.matchers.contains(ctx.primer.getBlockState(x, y, z))) {
                                if (conditions.noise.GetBoolean(x, z)) {
                                    ctx.primer.setBlockState(x, y, z, cfg.state);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
