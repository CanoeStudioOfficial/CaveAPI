package org.caveapi.world.generator;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.ArrayUtils;
import org.caveapi.Main;
import org.caveapi.config.ConfigFile;
import org.caveapi.noise.CachedNoiseHelper;
import org.caveapi.world.BiomeSearch;
import org.caveapi.world.GeneratorController;
import org.caveapi.world.HeightMapLocator;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class EarlyCaveHook extends MapGenBase {

    @Nullable MapGenBase priorCaves;

    public EarlyCaveHook(@Nullable MapGenBase priorCaves) {
        this.priorCaves = priorCaves;
    }

    @Override
    public void generate(World world, int x, int z, ChunkPrimer primer) {
        if (ConfigFile.otherGeneratorEnabled && this.priorCaves != null) {
            this.priorCaves.generate(world, x, z, primer);
        }
        // Don't really have a good way to access this without writing the game myself.
        final Map<String, GeneratorController> generators = Main.instance.loadGenerators(world);
        final int[][] heightmap = ArrayUtils.contains(ConfigFile.heightMapDims, world.provider.getDimension())
            ? HeightMapLocator.getHeightFromPrimer(primer)
            : HeightMapLocator.FAUX_MAP;

        final BiomeSearch biomes = BiomeSearch.in(world, x, z);

        //final BiomeSearch biomes = BiomeSearch.in(DimensionManager.getWorld(id), x, z);

        // Get biome searches for all proxy dimensions to add to context
        List<BiomeSearch> proxyBiomes = new ArrayList<>();
        proxyBiomes.add(biomes);
        for (int i = 0; i < ConfigFile.archaeneProxyDims.length; i++) {
            if(world.provider.getDimension() == ConfigFile.archaeneUseProxyDims[i]
              && ConfigFile.archaeneProxyDims[i] != ConfigFile.archaeneUseProxyDims[i]) {
                try {
                    final WorldProvider dim = DimensionManager.getProvider(ConfigFile.archaeneProxyDims[i]);
                }
                catch(NullPointerException e) {
                    DimensionManager.initDimension(ConfigFile.archaeneProxyDims[i]);
                }
                proxyBiomes.add(BiomeSearch.in2(DimensionManager.getProvider(ConfigFile.archaeneProxyDims[i]), x, z));
            }
        }
        final PrimerContext ctx = new PrimerContext(heightmap, world, x, z, primer, proxyBiomes);


        ctx.world.rand.setSeed(ctx.world.getSeed());
        for (GeneratorController generator : generators.values()) {
            generator.earlyGenerate(ctx);
            // No reason to split these in two
            //generator.mapGenerate(ctx);
        }
        CachedNoiseHelper.resetAll();
    }
}
