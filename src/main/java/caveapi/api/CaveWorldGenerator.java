package caveapi.api;


import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public abstract class CaveWorldGenerator implements IWorldGenerator {
    private final WorldGenerator noiseCarver;

    public CaveWorldGenerator() {
        this.noiseCarver = new MapGenCaveNoise();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (CaveConfig.generateNoiseCarvers) {
            noiseCarver.generate(world, random, chunkX << 4, 0, chunkZ << 4);
        }
    }
}