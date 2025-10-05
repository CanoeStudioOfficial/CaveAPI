package org.caveapi.world.feature;

import org.caveapi.Main;
import org.caveapi.config.ConfigFile;
import org.caveapi.world.GeneratorController;
import org.caveapi.world.HeightMapLocator;
import org.caveapi.world.data.ChunkStaging;
import lombok.extern.log4j.Log4j2;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.common.DimensionManager;

import java.util.*;

@Log4j2
public class FeatureCaveHook implements IWorldGenerator {

    //public static HashMap<ChunkPos, Integer> populatedChunks;

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen, IChunkProvider chunkProv) {
        // Once again, there is no way to avoid retrieving this statically.
        final Map<String, GeneratorController> generators = Main.instance.loadGenerators(world);
        final int[][] heightmap = HeightMapLocator.getHeightFromWorld(world, chunkX, chunkZ);

        final int centerX = chunkX*16 + 8;
        final int centerZ = chunkZ*16 + 8;
        //final Biome center = world.getBiomeProvider().getBiome(new BlockPos(centerX, 0, centerZ));
        //final Biome center = BiomeCache.getCachedBiome(centerX, centerZ, world.provider);

        // Get biome at the center of a chunk for all proxy dimensions to add to context
        List<Biome> proxyBiomes = new ArrayList<Biome>();
        for (int i = 0; i < ConfigFile.archaeneProxyDims.length; i++) {
            if(world.provider.getDimension() == ConfigFile.archaeneUseProxyDims[i]
                    && ConfigFile.archaeneProxyDims[i] != ConfigFile.archaeneUseProxyDims[i]) {
                //final BiomeProvider provider = DimensionManager.getWorld(ConfigFile.archaeneProxyDims[i]).getBiomeProvider();
                final WorldProvider provider = DimensionManager.getWorld(ConfigFile.archaeneProxyDims[i]).provider;
                proxyBiomes.add(provider.getBiomeProvider().getBiome(new BlockPos(chunkX, 0, chunkZ)));
                //proxyBiomes.add(BiomeCache.getCachedBiome(centerX, centerZ, provider));
            }
        }


        for (GeneratorController generator : generators.values()) {
            final WorldContext ctx = new WorldContext(heightmap, generator, rand, chunkX, chunkZ, world, proxyBiomes);
            generator.vanillaGenerate(ctx);
        }

        // Custom search pattern for chunks from the center outward.
        // 3x3 = 9
        // 5x5 = 25
        // 7x7 = 49 (max)
        final int[] searchX = {0, 1, 0, -1, 0, 1, 1, -1, -1, 2, 0, -2, 0, 2, 2, 1, -1, -2, -2, -1, 1, 2, -2, -2, 2, 3, 0, -3, 0, 3, 3, 1, -1, -3, -3, -1, 1, 3, 3, 2, -2, -3, -3, -2, 2, 3, 3, -3, -3};
        final int[] searchZ = {0, 0, -1, 0, 1, 1, -1, -1, 1, 0, -2, 0, 2, 1, -1, -2, -2, -1, 1, 2, 2, -2, -2, 2, 2, 0, -3, 0, 3, 1, -1, -3, -3, -1, 1, 3, 3, 2, -2, -3, -3, -2, 2, 3, 3, 3, -3, -3, 3};
        int[][] stagesCurrent = new int[9][9];
        int[][] stagesNew = new int[9][9];

        int stage = ChunkStaging.getChunkStaging(world, chunkX, chunkZ);
        if (stage == 0)
            ChunkStaging.setChunkStaging(world, chunkX, chunkZ, 2);

        //log.info("Current chunk stage: {}", stage);

        // Get current staging values and store them in a 9x9 area
        for (int cX = -4; cX <= 4; cX++) {
            for (int cZ = -4; cZ <= 4; cZ++) {
                if (chunkProv.isChunkGeneratedAt(chunkX+cX, chunkZ+cZ)) {
                    if (chunkProv.provideChunk(chunkX + cX, chunkZ + cZ).isTerrainPopulated()) {
                        int s = ChunkStaging.getChunkStaging(world, chunkX+cX, chunkZ+cZ);
                        stagesCurrent[cX + 4][cZ + 4] = s;
                        stagesNew[cX + 4][cZ + 4] = s;
                        /*
                        if (s == 0) {
                            stagesCurrent[cX + 4][cZ + 4] = 0;
                            stagesNew[cX + 4][cZ + 4] = 2;
                        } else {
                            stagesCurrent[cX + 4][cZ + 4] = s;
                            stagesNew[cX + 4][cZ + 4] = s;
                        }
                         */
                    }
                }
            }
        }

        //log.info("Reported current chunk stage: {}", stagesNew[4][4]);

        // Generate roads then large structures, 5x5 & 5x5
        for (int v = 0; v < 25; v++) {
            final int stageNr = 2;
            final int oX = searchX[v];
            final int oZ = searchZ[v];
            // only generate on stage 2
            if (stagesNew[oX+4][oZ+4] == stageNr) {
                // only generate if all nearby chunks are up to the same stage
                boolean pass = true;
                // check chunks in reverse order to try and fail faster
                for (int w = 24; w >= 0; w--) {
                    final int wX = searchX[w];
                    final int wZ = searchZ[w];
                    if (stagesNew[wX+4+oX][wZ+4+oZ] < stageNr) {
                        pass = false;
                        break;
                    }
                }
                if (pass) {
                    stagesNew[oX+4][oZ+4] = stageNr+1;
                    // Run road generation
                }
            }
        }
        // Generate large structures in all chunks that generated roads
        for (int v = 0; v < 25; v++) {
            final int stageNr = 3;
            final int oX = searchX[v];
            final int oZ = searchZ[v];
            // only generate on stage 2
            if (stagesNew[oX+4][oZ+4] == stageNr) {
                stagesNew[oX+4][oZ+4] = stageNr+1;
                for (GeneratorController generator : generators.values()) {
                    final WorldContext ctx = new WorldContext(heightmap, generator, rand, chunkX+oX, chunkZ+oZ, world, proxyBiomes);
                    generator.largeGenerate(ctx);
                }
            }
        }

        // Generate small structures then features, 7x7 & 3x3
        for (int v = 0; v < 49; v++) {
            final int stageNr = 4;
            final int oX = searchX[v];
            final int oZ = searchZ[v];
            // only generate on stage 4
            if (stagesNew[oX+4][oZ+4] == stageNr) {
                // only generate if all nearby chunks are up to the same stage
                boolean pass = true;
                // check chunks in reverse order to try and fail faster
                for (int w = 8; w >= 0; w--) {
                    final int wX = searchX[w];
                    final int wZ = searchZ[w];
                    if (stagesNew[wX+4+oX][wZ+4+oZ] < stageNr) {
                        pass = false;
                        break;
                    }
                }
                if (pass) {
                    stagesNew[oX+4][oZ+4] = stageNr+1;
                    for (GeneratorController generator : generators.values()) {
                        final WorldContext ctx = new WorldContext(heightmap, generator, rand, chunkX+oX, chunkZ+oZ, world, proxyBiomes);
                        generator.smallGenerate(ctx);
                    }
                }
            }
        }
        // Generate small features in all chunks that generated structures
        for (int v = 0; v < 49; v++) {
            final int stageNr = 5;
            final int oX = searchX[v];
            final int oZ = searchZ[v];
            // only generate on stage 5
            if (stagesNew[oX+4][oZ+4] == stageNr) {
                stagesNew[oX+4][oZ+4] = stageNr+1;
                // Run small feature generation

                // ~~~ Temp
                //log.info("Generating features at {}, {} from {}, {}", (chunkX+oX), (chunkZ+oZ), (chunkX), (chunkZ));
                for (GeneratorController generator : generators.values()) {
                    final WorldContext ctx = new WorldContext(heightmap, generator, rand, chunkX+oX, chunkZ+oZ, world, proxyBiomes);
                    generator.featureGenerate(ctx);
                }
                // ~~~
            }
        }

        /*
        log.info("Staging map pre:");
        int tX = 0;
        int tZ = 0;
        log.info("{} {} {} {} {} {} {} {} {}", stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ], stagesCurrent[tX++][tZ]);


        log.info("Staging map post:");
        tX = 0;
        tZ = 0;
        log.info("{} {} {} {} {} {} {} {} {}", stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ]);
        tX = 0; tZ++;
        log.info("{} {} {} {} {} {} {} {} {}", stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ], stagesNew[tX++][tZ]);

         */

        // Check changes from current to new stages
        for (int cX = -4; cX <= 4; cX++) {
            for (int cZ = -4; cZ <= 4; cZ++) {
                if (stagesCurrent[cX+4][cZ+4] == stagesNew[cX+4][cZ+4]) {
                    stagesNew[cX+4][cZ+4] = -1;
                } else {
                    ChunkStaging.setChunkStaging(world, chunkX+cX, chunkZ+cZ, stagesNew[cX+4][cZ+4]);
                }
            }
        }

        //CachedNoiseHelper.resetAll();
    }
}