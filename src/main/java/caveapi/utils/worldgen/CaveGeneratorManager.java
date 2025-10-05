package caveapi.utils.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

import java.util.*;

public class CaveGeneratorManager extends MapGenBase
{
    private final Map<String, CaveGenerator> biomeToGenerator;
    private final List<FallbackMappingRule> fallbackRules;
    private final MapGenBase vanillaCaveGen;

    public CaveGeneratorManager(final MapGenBase vanillaCaveGen) {
        this.biomeToGenerator = new HashMap<String, CaveGenerator>();
        this.fallbackRules = new ArrayList<FallbackMappingRule>();
        this.vanillaCaveGen = vanillaCaveGen;
    }

    public void registerGenerator(final String[] biomeNames, final CaveGenerator generator) {
        for (final String biomeName : biomeNames) {
            this.biomeToGenerator.put(biomeName, generator);
        }
    }

    public void generate(final World world, final int chunkX, final int chunkZ, final ChunkPrimer primer) {
        final BlockPos center = new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);
        final Biome biome = world.getBiome(center);
        final String biomeName = biome.getRegistryName().toString();
        CaveGenerator generator = this.biomeToGenerator.get(biomeName);
        if (generator == null) {
            for (final FallbackMappingRule rule : this.fallbackRules) {
                if (rule.matches(world, center, biomeName)) {
                    generator = rule.getGenerator();
                    break;
                }
            }
        }
        if (generator != null) {
            generator.generate(world, chunkX, chunkZ, primer);
        }
        else {
            this.vanillaCaveGen.generate(world, chunkX, chunkZ, primer);
        }
    }

    private static class FallbackMappingRule
    {
        private final String targetBiome;
        private final Set<String> neighborBiomes;
        private final CaveGenerator generator;

        public FallbackMappingRule(final String targetBiome, final Collection<String> neighborBiomes, final CaveGenerator generator) {
            this.targetBiome = targetBiome;
            this.neighborBiomes = new HashSet<String>(neighborBiomes);
            this.generator = generator;
        }

        public boolean matches(final World world, final BlockPos center, final String currentBiomeName) {
            if (!this.targetBiome.equals(currentBiomeName)) {
                return false;
            }
            for (int dx = -32; dx <= 32; dx += 8) {
                for (int dz = -32; dz <= 32; dz += 8) {
                    final BlockPos pos = center.add(dx, 0, dz);
                    final Biome neighbor = world.getBiome(pos);
                    final String neighborName = neighbor.getRegistryName().toString();
                    if (this.neighborBiomes.contains(neighborName)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public CaveGenerator getGenerator() {
            return this.generator;
        }
    }
}