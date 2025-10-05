package org.caveapi.world;

import com.personthecat.cavegenerator.world.generator.*;
import net.minecraft.world.biome.Biome;
import org.caveapi.world.generator.PrimerContext;

import java.util.ArrayList;
import java.util.List;


public class LayersBiomeArray {

    public List<Integer> layerBiomes = new ArrayList<>();
    public int[][] layerBiomeIds = new int[3][3];
    public int[] layerWeights = new int[9];
    public int[][] biomeArray = new int[16][16];


    public LayersBiomeArray(PrimerContext ctx, int index) {

        List<BiomeSearch.Data> data = new ArrayList<>();
        for (BiomeSearch.Data d : ctx.biomeSearches.get(index).surrounding.get()) {
            if (d.chunkX >= ctx.chunkX-1 && d.chunkZ >= ctx.chunkZ-1 && d.chunkX <= ctx.chunkX+1 && d.chunkZ <= ctx.chunkZ+1) {
                data.add(d);
                this.layerBiomeIds[d.chunkX - ctx.chunkX + 1][d.chunkZ - ctx.chunkZ + 1] = Biome.getIdForBiome(d.biome);
                if (!layerBiomes.contains(Biome.getIdForBiome(d.biome)))
                    layerBiomes.add(Biome.getIdForBiome(d.biome));
            }
        }
        for (int x = 0; x < 16; x++) {
            final int actualX = x + (ctx.chunkX * 16);
            for (int z = 0; z < 16; z++) {
                final int actualZ = z + (ctx.chunkZ * 16);
                for (BiomeSearch.Data d : data) {
                    int taxicab = 31 - (Math.abs(actualX - d.centerX) + Math.abs(actualZ - d.centerZ));
                    if (taxicab < 1) {
                        continue;
                    }
                    int biome = this.layerBiomeIds[d.chunkX - ctx.chunkX + 1][d.chunkZ - ctx.chunkZ + 1];
                    for (int w = 0; w < layerBiomes.size(); w++) {
                        if (this.layerBiomes.get(w) == biome) {
                            this.layerWeights[w]+=taxicab;
                            break;
                        }
                    }
                }
                int highest = -1000;
                int biome = -1000;
                for (int w = 0; w < this.layerBiomes.size(); w++) {
                    if (this.layerWeights[w] > highest) {
                        highest = this.layerWeights[w];
                        biome = this.layerBiomes.get(w);
                    }
                    this.layerWeights[w] = 0;
                }
                this.biomeArray[x][z] = biome;
            }
        }
    }
}
