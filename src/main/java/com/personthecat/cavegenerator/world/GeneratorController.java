package com.personthecat.cavegenerator.world;

import com.personthecat.cavegenerator.config.CavePreset;
import com.personthecat.cavegenerator.config.ConfigFile;
import com.personthecat.cavegenerator.data.*;
import com.personthecat.cavegenerator.world.feature.*;
import com.personthecat.cavegenerator.world.generator.*;
import com.personthecat.cavegenerator.world.roads.RoadGenerator;
import lombok.Builder;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;

import java.util.*;

import static com.personthecat.cavegenerator.util.CommonMethods.map;

// Please forgive me, Cat, for what I have done here.
@Builder
public class GeneratorController {
    public final ClusterGenerator globalClusters;
    public final ClusterGenerator layeredClusters;

    public List<ClusterGenerator> globalClustersList;
    public List<ClusterGenerator> layeredClustersList;
    public List<TunnelGenerator> tunnels;
    public List<RavineGenerator> ravines;
    public List<CavernGenerator> caverns;
    public List<CavernGenerator> deferredCaverns;
    public List<BurrowGenerator> burrows;
    public List<LayerGenerator> layers;
    public List<LayerGenerator> deferredLayers;
    public List<TunnelConnector<CavernGenerator>> cavernTunnels;
    public List<TunnelConnector<BurrowGenerator>> burrowTunnels;
    public List<StructureGenerator> structuresVanilla;
    // Roads
    public List<RoadGenerator> roads;
    public List<StructureGenerator> structuresLarge;
    // Structures
    public List<StructureGenerator> structuresSmall;
    // Small features
    public List<StructureGenerator> structuresFeature;
    public List<StalactiteGenerator> stalactites;
    public List<PillarGenerator> pillars;

    private final HashMap<Integer, DimensionGeneratorCache> dimensionCache = new HashMap<>();

    public static boolean reloaded = false;

    public static GeneratorController from(CavePreset preset, World world) {
        final GeneratorControllerBuilder builder = builder()
            .tunnels(map(preset.tunnels, t -> new TunnelGenerator(t, world)))
            .ravines(map(preset.ravines, r -> new RavineGenerator(r, world)))
            //.layers(map(preset.layers, l -> new LayerGenerator(l, world)))
            .stalactites(map(preset.stalactites, s -> new StalactiteGenerator(s, world)))
            .pillars(map(preset.pillars, p -> new PillarGenerator(p, world)))
            .roads(map(preset.roads, r -> new RoadGenerator(r, world)));
        sortClusters(preset.clusters, world, builder);
        mapStructures(preset.structures, world, builder);
        mapLayers(preset.layers, world, builder);
        mapCaverns(preset.caverns, world, builder);
        mapBurrows(preset.burrows, world, builder);
        return builder.build();
    }

    public static GeneratorController from(Map<String, GeneratorController> generators) {

        List<ClusterGenerator> allGlobalClustersList = new ArrayList<>();
        List<ClusterGenerator> allLayeredClustersList = new ArrayList<>();
        List<TunnelGenerator> allTunnels = new ArrayList<>();
        List<RavineGenerator> allRavines = new ArrayList<>();
        List<CavernGenerator> allCaverns = new ArrayList<>();
        List<CavernGenerator> allDeferredCaverns = new ArrayList<>();
        List<BurrowGenerator> allBurrows = new ArrayList<>();
        List<LayerGenerator> allLayers = new ArrayList<>();
        List<LayerGenerator> allDeferredLayers = new ArrayList<>();
        List<TunnelConnector<CavernGenerator>> allCavernTunnels = new ArrayList<>();
        List<TunnelConnector<BurrowGenerator>> allBurrowTunnels = new ArrayList<>();
        List<StalactiteGenerator> allStalactites = new ArrayList<>();
        List<PillarGenerator> allPillars = new ArrayList<>();
        List<StructureGenerator> allStructuresVanilla = new ArrayList<>();
        List<StructureGenerator> allStructuresLarge = new ArrayList<>();
        List<StructureGenerator> allStructuresSmall = new ArrayList<>();
        List<StructureGenerator> allStructuresFeature = new ArrayList<>();

        for (GeneratorController generator : generators.values()) {
            allGlobalClustersList.add(generator.globalClusters);
            allLayeredClustersList.add(generator.layeredClusters);
            allTunnels.addAll(generator.tunnels);
            allRavines.addAll(generator.ravines);
            allCaverns.addAll(generator.caverns);
            allDeferredCaverns.addAll(generator.deferredCaverns);
            allBurrows.addAll(generator.burrows);
            allLayers.addAll(generator.layers);
            allDeferredLayers.addAll(generator.deferredLayers);
            allCavernTunnels.addAll(generator.cavernTunnels);
            allBurrowTunnels.addAll(generator.burrowTunnels);
            allStalactites.addAll(generator.stalactites);
            allPillars.addAll(generator.pillars);
            allStructuresVanilla.addAll(generator.structuresVanilla);
            allStructuresLarge.addAll(generator.structuresLarge);
            allStructuresSmall.addAll(generator.structuresSmall);
            allStructuresFeature.addAll(generator.structuresFeature);
        }

        final GeneratorControllerBuilder builder = builder()
                .globalClustersList(allGlobalClustersList)
                .layeredClustersList(allLayeredClustersList)
                .tunnels(allTunnels)
                .ravines(allRavines)
                .caverns(allCaverns)
                .deferredCaverns(allDeferredCaverns)
                .burrows(allBurrows)
                .layers(allLayers)
                .deferredLayers(allDeferredLayers)
                .cavernTunnels(allCavernTunnels)
                .burrowTunnels(allBurrowTunnels)
                .stalactites(allStalactites)
                .pillars(allPillars)
                .structuresVanilla(allStructuresVanilla)
                .structuresLarge(allStructuresLarge)
                .structuresSmall(allStructuresSmall)
                .structuresFeature(allStructuresFeature);
        return builder.build();
    }

    private static void mapStructures(List<StructureSettings> structs, World world, GeneratorControllerBuilder builder) {
        final List<StructureGenerator> sv = new ArrayList<>();
        final List<StructureGenerator> sl = new ArrayList<>();
        final List<StructureGenerator> ss = new ArrayList<>();
        final List<StructureGenerator> sf = new ArrayList<>();
        for (StructureSettings struct : structs) {
            final StructureGenerator generator = new StructureGenerator(struct, world);
            (struct.archaeneSize == 1 ? sl : struct.archaeneSize == 2 ? ss : struct.archaeneSize == 3 ? sf : sv).add(generator);
        }
        builder.structuresVanilla(sv);
        builder.structuresLarge(sl);
        builder.structuresSmall(ss);
        builder.structuresFeature(sf);
    }

    private static void sortClusters(List<ClusterSettings> clusters, World world, GeneratorControllerBuilder builder) {
        final List<ClusterSettings> global = new ArrayList<>();
        final List<ClusterSettings> layered = new ArrayList<>();
        clusters.forEach(c -> (c.matchers.isEmpty() ? global : layered).add(c));
        builder.globalClusters(new ClusterGenerator(global, world))
            .layeredClusters(new ClusterGenerator(layered, world));
    }

    private static void mapLayers(List<LayerSettings> layers, World world, GeneratorControllerBuilder builder) {
        final List<LayerGenerator> generators = new ArrayList<>();
        final List<LayerGenerator> deferred = new ArrayList<>();
        for (LayerSettings layer : layers) {
            final LayerGenerator generator = new LayerGenerator(layer, world);
            (layer.deferred ? deferred : generators).add(generator);
        }
        builder.layers(generators);
        builder.deferredLayers(deferred);
    }

    private static void mapCaverns(List<CavernSettings> caverns, World world, GeneratorControllerBuilder builder) {
        final List<CavernGenerator> generators = new ArrayList<>();
        final List<CavernGenerator> deferred = new ArrayList<>();

        final List<TunnelConnector<CavernGenerator>> connectors = new ArrayList<>();
        for (CavernSettings cavern : caverns) {
            final CavernGenerator generator = new CavernGenerator(cavern, world);
            (cavern.deferred ? deferred : generators).add(generator);
            cavern.branches.ifPresent(b -> connectors.add(new TunnelConnector<>(b, generator, world)));
        }
        builder.caverns(generators);
        builder.deferredCaverns(deferred);
        builder.cavernTunnels(connectors);
    }

    private static void mapBurrows(List<BurrowSettings> burrows, World world, GeneratorControllerBuilder builder) {
        final List<BurrowGenerator> generators = new ArrayList<>();
        final List<TunnelConnector<BurrowGenerator>> connectors = new ArrayList<>();
        for (BurrowSettings burrow : burrows) {
            final BurrowGenerator generator = new BurrowGenerator(burrow, world);
            generators.add(generator);
            burrow.branches.ifPresent(b -> connectors.add(new TunnelConnector<>(b, generator, world)));
        }
        builder.burrows(generators);
        builder.burrowTunnels(connectors);
    }

    /** Generate noise-based features in the world before anything else. */
    public void earlyGenerate(PrimerContext ctx) {
        if (reloaded) {
            this.dimensionCache.clear();
            reloaded = false;
        }

        int id = ctx.world.provider.getDimension();
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        ids.addAll(this.getProxyIDs(id));
        int s;

        List<Integer> biomeIds = new ArrayList<>();
        for (BiomeSearch search : ctx.biomeSearches) {
            biomeIds.add(Biome.getIdForBiome(search.current));
        }

        // Make sure stuff gets initialized
        for (int i : ids) {
            if (this.dimensionCache.get(i) == null)
                this.dimensionCache.put(i, new DimensionGeneratorCache());
        }

        //globalClusters.generate(ctx);
        globalClustersList.forEach(l -> l.generate(ctx));

        // generate layers biome map
        List<LayersBiomeArray> layerBiomes = new ArrayList<>();

        if (this.layers.size() > 0) {
            // TODO: only generate arrays for relevant dimensions
            s = 0;
            for (int i : ids) {
                layerBiomes.add(new LayersBiomeArray(ctx, s));
                s++;
            }
            layers.forEach(l -> l.generate(ctx, layerBiomes));
        }

        //layeredClusters.generate(ctx);
        layeredClustersList.forEach(l -> l.generate(ctx));

        if (this.caverns.size() > 0) {
            // Initialize Caverns
            s = 0;
            for (int i : ids) {
                // Check if the cache has a key
                if (this.dimensionCache.get(i).cavernsCache.get(biomeIds.get(s)) == null) {
                    // If no, add a key and check each preset for validity to add it to the new key
                    this.dimensionCache.get(i).cavernsCache.put(biomeIds.get(s), new ArrayList<>());
                    int S = s;
                    caverns.forEach(c -> {
                        int proxy = c.cfg.conditions.proxyDimension;
                        if (S == proxy && c.checkGeneration(ctx))
                            this.dimensionCache.get(ids.get(proxy)).cavernsCache.get(biomeIds.get(proxy)).add(c);
                    });
                }
                s++;
            }
            // Generate Caverns
            s = 0;
            for (int i : ids) {
                dimensionCache.get(i).cavernsCache.get(biomeIds.get(s)).forEach(c -> c.generate(ctx));
                s++;
            }
        }

        if (this.burrows.size() > 0) {
            // Initialize burrows
            s = 0;
            for (int i : ids) {
                // Check if the cache has a key
                if (dimensionCache.get(i).burrowsCache.get(biomeIds.get(s)) == null) {
                    // If no, add a key and check each preset for validity to add it to the new key
                    dimensionCache.get(i).burrowsCache.put(biomeIds.get(s), new ArrayList<>());
                    int S = s;
                    burrows.forEach(c -> {
                        int proxy = c.cfg.conditions.proxyDimension;
                        if (S == proxy && c.checkGeneration(ctx))
                            dimensionCache.get(ids.get(proxy)).burrowsCache.get(biomeIds.get(proxy)).add(c);
                    });
                }
                s++;
            }
            // Generate burrows
            s = 0;
            for (int i : ids) {
                dimensionCache.get(i).burrowsCache.get(biomeIds.get(s)).forEach(c -> c.generate(ctx));
                s++;
            }
        }

        if (this.tunnels.size() > 0) {
            // Initialize Tunnels
            s = 0;
            for (int i : ids) {
                // Check if the cache has a key
                if (dimensionCache.get(i).tunnelsCache.get(biomeIds.get(s)) == null) {
                    // If no, add a key and check each preset for validity to add it to the new key
                    dimensionCache.get(i).tunnelsCache.put(biomeIds.get(s), new ArrayList<>());
                    int S = s;
                    tunnels.forEach(t -> {
                        int proxy = t.cfg.conditions.proxyDimension;
                        if(S == proxy && t.checkGeneration(ctx))
                            dimensionCache.get(ids.get(proxy)).tunnelsCache.get(biomeIds.get(proxy)).add(t);
                    });
                }
                s++;
            }

            // Generate Tunnels
            s = 0;
            for (int i : ids) {
                dimensionCache.get(i).tunnelsCache.get(biomeIds.get(s)).forEach(t -> t.generate(ctx));
                s++;
            }
        }

        if (this.ravines.size() > 0) {
            // Initialize ravines
            s = 0;
            for (int i : ids) {
                // Check if the cache has a key
                if (dimensionCache.get(i).ravinesCache.get(biomeIds.get(s)) == null) {
                    // If no, add a key and check each preset for validity to add it to the new key
                    dimensionCache.get(i).ravinesCache.put(biomeIds.get(s), new ArrayList<>());
                    int S = s;
                    ravines.forEach(c -> {
                        int proxy = c.cfg.conditions.proxyDimension;
                        if (S == proxy && c.checkGeneration(ctx))
                            dimensionCache.get(ids.get(proxy)).ravinesCache.get(biomeIds.get(proxy)).add(c);
                    });
                }
                s++;
            }
            // Generate ravines
            s = 0;
            for (int i : ids) {
                dimensionCache.get(i).ravinesCache.get(biomeIds.get(s)).forEach(c -> c.generate(ctx));
                s++;
            }
        }

        if (this.cavernTunnels.size() > 0) {
            // Initialize cavernTunnels
            s = 0;
            for (int i : ids) {
                // Check if the cache has a key
                if (dimensionCache.get(i).cavernTunnelsCache.get(biomeIds.get(s)) == null) {
                    // If no, add a key and check each preset for validity to add it to the new key
                    dimensionCache.get(i).cavernTunnelsCache.put(biomeIds.get(s), new ArrayList<>());
                    int S = s;
                    cavernTunnels.forEach(c -> {
                        int proxy = c.cfg.conditions.proxyDimension;
                        if (S == proxy && c.checkGeneration(ctx))
                            dimensionCache.get(ids.get(proxy)).cavernTunnelsCache.get(biomeIds.get(proxy)).add(c);
                    });
                }
                s++;
            }
            // Generate cavernTunnels
            s = 0;
            for (int i : ids) {
                dimensionCache.get(i).cavernTunnelsCache.get(biomeIds.get(s)).forEach(c -> c.generate(ctx));
                s++;
            }
        }

        if (this.burrowTunnels.size() > 0) {
            // Initialize burrowTunnels
            s = 0;
            for (int i : ids) {
                // Check if the cache has a key
                if (dimensionCache.get(i).burrowTunnelsCache.get(biomeIds.get(s)) == null) {
                    // If no, add a key and check each preset for validity to add it to the new key
                    dimensionCache.get(i).burrowTunnelsCache.put(biomeIds.get(s), new ArrayList<>());
                    int S = s;
                    burrowTunnels.forEach(c -> {
                        int proxy = c.cfg.conditions.proxyDimension;
                        if (S == proxy && c.checkGeneration(ctx))
                            dimensionCache.get(ids.get(proxy)).burrowTunnelsCache.get(biomeIds.get(proxy)).add(c);
                    });
                }
                s++;
            }
            // Generate burrowTunnels
            s = 0;
            for (int i : ids) {
                dimensionCache.get(i).burrowTunnelsCache.get(biomeIds.get(s)).forEach(c -> c.generate(ctx));
                s++;
            }
        }

        if (this.deferredCaverns.size() > 0) {
            // Initialize deferredCaverns
            s = 0;
            for (int i : ids) {
                // Check if the cache has a key
                if (dimensionCache.get(i).deferredCavernsCache.get(biomeIds.get(s)) == null) {
                    // If no, add a key and check each preset for validity to add it to the new key
                    dimensionCache.get(i).deferredCavernsCache.put(biomeIds.get(s), new ArrayList<>());
                    int S = s;
                    deferredCaverns.forEach(c -> {
                        int proxy = c.cfg.conditions.proxyDimension;
                        if (S == proxy && c.checkGeneration(ctx))
                            dimensionCache.get(ids.get(proxy)).deferredCavernsCache.get(biomeIds.get(proxy)).add(c);
                    });
                }
                s++;
            }
            // Generate deferredCaverns
            s = 0;
            for (int i : ids) {
                dimensionCache.get(i).deferredCavernsCache.get(biomeIds.get(s)).forEach(c -> c.generate(ctx));
                s++;
            }
        }

        deferredLayers.forEach(l -> l.generate(ctx, layerBiomes));

    }

    public List<Integer> getProxyIDs(int id) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < ConfigFile.archaeneProxyDims.length; i++) {
            if(id == ConfigFile.archaeneUseProxyDims[i]
                    && ConfigFile.archaeneProxyDims[i] != ConfigFile.archaeneUseProxyDims[i]) {
                try {
                    final WorldProvider dim = DimensionManager.getProvider(ConfigFile.archaeneProxyDims[i]);
                }
                catch(NullPointerException e) {
                    DimensionManager.initDimension(ConfigFile.archaeneProxyDims[i]);
                }
                ids.add(ConfigFile.archaeneProxyDims[i]);
            }
        }
        return ids;
    }

    /** Spawn all of the superficial decorations that take place later in the chunk generation cycle. */
    public void roadGenerate(WorldContext ctx) {
        //roads.forEach(r -> r.generate(ctx));
    }

    /** Spawn all of the superficial decorations that take place later in the chunk generation cycle. */
    public void vanillaGenerate(WorldContext ctx) {
        structuresVanilla.forEach(s -> s.generate(ctx));
    }

    /** Spawn all of the superficial decorations that take place later in the chunk generation cycle. */
    public void largeGenerate(WorldContext ctx) {
        structuresLarge.forEach(s -> s.generate(ctx));
    }

    /** Spawn all of the superficial decorations that take place later in the chunk generation cycle. */
    public void smallGenerate(WorldContext ctx) {
        structuresSmall.forEach(s -> s.generate(ctx));
    }

    /** Spawn all of the superficial decorations that take place later in the chunk generation cycle. */
    public void featureGenerate(WorldContext ctx) {
        pillars.forEach(p -> p.generate(ctx));
        stalactites.forEach(s -> s.generate(ctx));
        structuresFeature.forEach(s -> s.generate(ctx));
    }

}
