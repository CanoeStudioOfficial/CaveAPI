package caveapi.world;

import caveapi.api.CaveGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

import java.util.*;

public class CaveGeneratorManager extends MapGenBase {
    private final Map<String, CaveGenerator> biomeToGenerator = new HashMap<>();
    private final List<FallbackMappingRule> fallbackRules = new ArrayList<>();
    private final MapGenBase vanillaCaveGen;

    public CaveGeneratorManager(MapGenBase vanillaCaveGen) {
        this.vanillaCaveGen = vanillaCaveGen;
    }

    // 新增的后备规则注册方法
    public void addFallbackRule(String targetBiome, Collection<String> neighborBiomes, CaveGenerator generator) {
        this.fallbackRules.add(new FallbackMappingRule(targetBiome, neighborBiomes, generator));
    }

    public void registerGenerator(String[] biomeNames, CaveGenerator generator) {
        for (String biomeName : biomeNames) {
            this.biomeToGenerator.put(biomeName, generator);
        }
    }

    public void generate(World world, int chunkX, int chunkZ, ChunkPrimer primer) {
        BlockPos center = new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);
        Biome biome = world.getBiome(center);
        String biomeName = biome.getRegistryName().toString();

        CaveGenerator generator = this.biomeToGenerator.get(biomeName);

        if (generator == null) {
            for (FallbackMappingRule rule : this.fallbackRules) {
                if (rule.matches(world, center, biomeName)) {
                    generator = rule.getGenerator();
                    break;
                }
            }
        }

        if (generator != null) {
            generator.generate(world, chunkX, chunkZ, primer);
        } else {
            this.vanillaCaveGen.generate(world, chunkX, chunkZ, primer);
        }
    }

    // 将内部类改为包级可见
    static class FallbackMappingRule {
        private final String targetBiome;
        private final Set<String> neighborBiomes;
        private final CaveGenerator generator;

        public FallbackMappingRule(String targetBiome, Collection<String> neighborBiomes, CaveGenerator generator) {
            this.targetBiome = targetBiome;
            this.neighborBiomes = new HashSet<>(neighborBiomes);
            this.generator = generator;
        }

        public boolean matches(World world, BlockPos center, String currentBiomeName) {
            if (!this.targetBiome.equals(currentBiomeName)) {
                return false;
            }

            for (int dx = -32; dx <= 32; dx += 8) {
                for (int dz = -32; dz <= 32; dz += 8) {
                    BlockPos pos = center.add(dx, 0, dz);
                    Biome neighbor = world.getBiome(pos);
                    String neighborName = neighbor.getRegistryName().toString();
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