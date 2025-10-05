package org.caveapi.world;

import org.caveapi.config.ConfigFile;
import org.caveapi.util.Lazy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

import java.util.function.Predicate;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BiomeSearch {
    public final Biome current;
    //public final Lazy<Biome[]> current;
    public final Lazy<Data[]> surrounding;
    //public final Lazy<Biome[]> center;

    /** todo - unfluck this whole thing, should just pass in a BiomeProvider directly */

    /** Checks the surrounding biomes for the first match when given a predicate. */
    public boolean anyMatches(Predicate<Biome> predicate) {
        //for (Biome b : current.get()) {
        if (predicate.test(current)) {
            return true;
        }
        //}
        return false;
    }

    /** A public accessor to calculate the current biome array size. */
    public static int size() {
        final int d = ConfigFile.biomeRange * 2 + 1;
        return d * d;
    }

    /** Acquires the chunk's biome and surrounding biomes. */
    public static BiomeSearch in(World world, int x, int z) {
        //final Lazy<Biome[]> current = Lazy.of(() -> inner(world, x, z));
        final Biome current = inner(world, x, z);
        final Lazy<Data[]> surrounding = Lazy.of(() -> outer(world, x, z, ConfigFile.biomeRange));
        //Blue's change - Get the biome at the center of the chunk, too.
        //final Lazy<Biome[]> center = Lazy.of(() -> center(world, x, z));
        //return new BiomeSearch(current, surrounding, center);
        return new BiomeSearch(current, surrounding);
    }
    /** Acquires proxy biomes. */
    public static BiomeSearch in2(WorldProvider world, int x, int z) {
        //final Lazy<Biome[]> current = Lazy.of(() -> inner2(world, x, z));
        final Biome current = inner2(world, x, z);
        final Lazy<Data[]> surrounding = Lazy.of(() -> outer2(world, x, z, ConfigFile.biomeRange));
        //Blue's change - Get the biome at the center of the chunk, too.
        //final Lazy<Biome[]> center = Lazy.of(() -> center(world, x, z));
        //return new BiomeSearch(current, surrounding, center);
        return new BiomeSearch(current, surrounding);
    }

    /*
    private static Biome[] center(World world, int x, int z) {
        final BiomeProvider provider = world.getBiomeProvider();
        final int actualX = x << 4;
        final int actualZ = z << 4;
        final List<Biome> biomes = Arrays.asList(
                provider.getBiome(new BlockPos(actualX + 8, 0, actualZ + 8))
        );
        return new HashSet<>(biomes).toArray(new Biome[0]);
    }
    */

    /** Accumulates a list of biomes at the four corners of this chunk. */
    //private static Biome[] inner(World world, int x, int z) {
    private static Biome inner(World world, int x, int z) {
        final BiomeProvider provider = world.getBiomeProvider();
        final int actualX = x << 4;
        final int actualZ = z << 4;
        // This is only used for early generators, at which point the current
        // chunk does not yet exist. As a result, this is more direct.
        /*
        final List<Biome> biomes = Arrays.asList(
            provider.getBiome(new BlockPos(actualX + 1, 0, actualZ + 1)),
            provider.getBiome(new BlockPos(actualX + 1, 0, actualZ + 14)),
            provider.getBiome(new BlockPos(actualX + 14, 0, actualZ + 1)),
            provider.getBiome(new BlockPos(actualX + 14, 0, actualZ + 14))
        );
         */
        //final List<Biome> biomes = Arrays.asList(
        //    provider.getBiome(new BlockPos(actualX + 8, 0, actualZ + 8))
            //BiomeCache.getCachedBiome(actualX + 8, actualZ + 8, world.provider)
        //);

        // Remove redundant entries.
        //return new HashSet<>(biomes).toArray(new Biome[0]);
        return provider.getBiome(new BlockPos(actualX + 8, 0, actualZ + 8));
    }

    /** Checks outward in a range of <code>r</code> for surrounding center biomes. */
    private static Data[] outer(World world, int x, int z, int r) {
        final int d = r * 2 + 1;
        final Data[] biomes = new Data[d * d];
        int index = 0;
        for (int cX = x - r; cX <= x + r; cX++) {
            for (int cZ = z - r; cZ <= z + r; cZ++) {
                biomes[index++] = Data.create(world, cX, cZ);
            }
        }
        return biomes;
    }
    /** Acquires the proxy biome for the chunk. */
    //private static Biome[] inner2(WorldProvider world, int x, int z) {
    private static Biome inner2(WorldProvider world, int x, int z) {
        final BiomeProvider provider = world.getBiomeProvider();
        //final int actualX = x << 4;
        //final int actualZ = z << 4;
        //final List<Biome> biomes = Arrays.asList(
        //    provider.getBiome(new BlockPos(x, 0, z))
            //BiomeCache.getCachedBiome(actualX + 8, actualZ + 8, world)
        //);
        // Remove redundant entries.
        //return new HashSet<>(biomes).toArray(new Biome[0]);
        return provider.getBiome(new BlockPos(x, 0, z));
    }

    /** Acquires the proxy biome for surrounding chunks. */
    private static Data[] outer2(WorldProvider world, int x, int z, int r) {
        final int d = r * 2 + 1;
        final Data[] biomes = new Data[d * d];
        int index = 0;
        for (int cX = x - r; cX <= x + r; cX++) {
            for (int cZ = z - r; cZ <= z + r; cZ++) {
                biomes[index++] = Data.create2(world, cX, cZ);
            }
        }
        return biomes;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Data {
        public final Biome biome;
        public final int chunkX;
        public final int chunkZ;
        public final int centerX;
        public final int centerZ;

        private static Data create(World world, int chunkX, int chunkZ) {
            final int centerX = (chunkX << 4) + 8;
            final int centerZ = (chunkZ << 4) + 8;
            final Biome biome = world.getBiomeProvider().getBiome(new BlockPos(centerX, 0, centerZ));
            //final Biome biome = BiomeCache.getCachedBiome(centerX, centerZ, world.provider);
            return new Data(biome, chunkX, chunkZ, centerX, centerZ);
        }

        private static Data create2(WorldProvider world, int chunkX, int chunkZ) {
            final int centerX = (chunkX << 4) + 8;
            final int centerZ = (chunkZ << 4) + 8;
            final Biome biome = world.getBiomeProvider().getBiome(new BlockPos(chunkX, 0, chunkZ));
            //final Biome biome = BiomeCache.getCachedBiome(centerX, centerZ, world);

            return new Data(biome, chunkX, chunkZ, centerX, centerZ);
        }
    }
}
