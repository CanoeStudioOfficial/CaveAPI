package caveapi.cavegenerator.world.feature;

import caveapi.cavegenerator.Main;
import caveapi.cavegenerator.noise.CachedNoiseHelper;
import caveapi.cavegenerator.world.GeneratorController;
import caveapi.cavegenerator.world.HeightMapLocator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Map;
import java.util.Random;

public class FeatureCaveHook implements IWorldGenerator {

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen, IChunkProvider chunkProv) {
        // Once again, there is no way to avoid retrieving this statically.
        final Map<String, GeneratorController> generators = Main.instance.loadGenerators(world);
        final int[][] heightmap = HeightMapLocator.getHeightFromWorld(world, chunkX, chunkZ);

        for (GeneratorController generator : generators.values()) {
            final WorldContext ctx = new WorldContext(heightmap, generator, rand, chunkX, chunkZ, world);
            generator.featureGenerate(ctx);
        }
        CachedNoiseHelper.resetAll();
    }
}