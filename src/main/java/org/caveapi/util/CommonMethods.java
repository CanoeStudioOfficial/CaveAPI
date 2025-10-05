package org.caveapi.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** A collection of methods and functions to be imported into most classes. */
public class CommonMethods {

    /** Returns a clean-looking, general-purpose RuntimeException. */
    public static RuntimeException runEx(String x) {
        return new RuntimeException(x);
    }

    public static RuntimeException runEx(String x, Throwable t) {
        return new RuntimeException(x, t);
    }

    /** Shorthand for a RuntimeException using String#format. */
    public static RuntimeException runExF(String x, Object... args) {
        return new RuntimeException(f(x, args));
    }

    /** A neater way to interpolate strings. */
    public static String f(String s, Object... args) {
        int begin = 0, si = 0, oi = 0;
        StringBuilder sb = new StringBuilder();
        while (true) {
            si = s.indexOf("{}", si);
            if (si >= 0) {
                sb.append(s, begin, si);
                sb.append(args[oi++]);
                begin = si = si + 2;
            } else {
                break;
            }
        }
        sb.append(s.substring(begin));
        return sb.toString();
    }

    /**
     * Uses a linear search algorithm to locate a value in an array,
     * matching the predicate `by`. Shorthand for Stream#findFirst.
     *
     * Example:
     *  // Find x by x.name
     *  Object[] vars = getObjectsWithNames();
     *  Optional<Object> var = find(vars, (x) -> x.name.equals("Cat"));
     *  // You can then get the value -> NPE
     *  Object result = var.get()
     *  // Or use an alternative. Standard java.util.Optional. -> no NPE
     *  Object result = var.orElse(new Object("Cat"))
     */
    public static <T> Optional<T> find(T[] values, Predicate<T> by) {
        for (T val : values) {
            if (by.test(val)) {
                return full(val);
            }
        }
        return empty();
    }

    public static <T> Optional<T> find(Collection<T> values, Predicate<T> by) {
        for (T val : values) {
            if (by.test(val)) {
                return full(val);
            }
        }
        return empty();
    }

    /** Maps the given list to an {@link ArrayList} of a new type. */
    public static <T, U> List<U> map(List<T> list, Function<T, U> mapper) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * Converts a generic List into its standard array counterpart.
     * Unsafe. Should not be used for any primitive data type. In
     * Most cases where this method is used, storing the data in a
     * primitive array probably offers little or no benefit. As a
     * result, I may try to remove this sometime in the near future.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list, Class<T> clazz) {
        return list.toArray((T[]) Array.newInstance(clazz, 0));
    }

    /** Safely retrieves a value from the input map. */
    public static <K, V> Optional<V> safeGet(Map<K, V> map, K key) {
        return Optional.ofNullable(map.get(key));
    }

    /** Determines the extension of the input `file`. */
    public static String extension(final File file) {
        return extension(file.getName());
    }

    public static String extension(final String filename) {
        final String[] split = filename.split(Pattern.quote("."));
        return split.length == 1 ? "" : split[split.length - 1];
    }

    /** Gets the file name, minus the extension. */
    public static String noExtension(File file) {
        return noExtension(file.getName());
    }

    /** Removes any extensions from the input filename. */
    public static String noExtension(String name) {
        return name.split(Pattern.quote("."))[0];
    }

    /** Shorthand for calling Optional#empty. */
    public static <T> Optional<T> empty() {
        return Optional.empty();
    }

    /**
     * Shorthand for calling Optional#of, matching the existing syntax of
     * `empty`, while being more clear than `of` alone.
     */
    public static <T> Optional<T> full(T val) {
        return Optional.of(val);
    }

    public static <T> Optional<T> nullable(T val) {
        return Optional.ofNullable(val);
    }

    /** Returns a random number between the input bounds, inclusive. */
    public static int numBetween(Random rand, int min, int max) {
        return min == max ? min : rand.nextInt(max - min + 1) + min;
    }

    /** Returns a random number between the input bounds, inclusive. */
    public static float numBetween(Random rand, float min, float max) {
        return min == max ? min : rand.nextFloat() * (max - min) + min;
    }

    /** Variant of Arrays#sort which returns the array. */
    public static int[] sort(int[] array) {
        Arrays.sort(array);
        return array;
    }

    public static float[] sort(float[] array) {
        Arrays.sort(array);
        return array;
    }

    /** Divides 1 / `value` without any divide by zero errors or unsightly casting. */
    public static int invert(double value) {
        return value == 0 ? Integer.MAX_VALUE : (int) (1 / value);
    }

    /**
     * Used for retrieving a Biome from either a registry name
     * or unique ID. Returns an Optional<Biome> to ensure that
     * null checks are propagated elsewhere.
     */
    public static Optional<Biome> getBiome(String biomeName) {
        return Optional.ofNullable(ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName)));
    }

    public static Optional<Biome> getBiome(int biomeNumber) {
        return Optional.ofNullable((Biome.getBiomeForId(biomeNumber)));
    }

    public static Biome[] getBiomes(Type biomeType) {
        return BiomeDictionary.getBiomes(biomeType).toArray(new Biome[0]);
    }

    public static Type getBiomeType(String name) {
        return Type.getType(name);
    }

    /**
     * Variant of ForgeRegistries::BLOCKS#getValue that does not substitute
     * air for blocks that aren't found. Using Optional to improve null-safety.
     */
    public static Optional<IBlockState> getBlockState(String registryName) {
        // Ensure that air is returned if that is the query.
        if (registryName.equals("air") || registryName.equals("minecraft:air")) {
            return full(Blocks.AIR.getDefaultState());
        }

        // View the components of this string separately.
        final String[] split = registryName.split(":");

        // Ensure the number of segments to be valid.
        if (!(split.length > 0 && split.length < 4)) {
            throw runExF("Syntax error: could not determine block state from {}", registryName);
        }

        // Use the end section to determine the format.
        final String end = split[split.length - 1];

        // If the end of the string is numeric, it must be the metadata.
        if (StringUtils.isNumeric(end)) {
            final int meta = Integer.parseInt(end);
            final String updated = registryName.replace(":" + end, "");
            return _getBlock(updated, meta);
        }
        // The end isn't numeric, so the name is in the standard format.
        return _getBlock(registryName, 0);
    }

    /**
     * Internal variant of ForgeRegistries::BLOCKS#getValue that does not
     * return air. This ensures that a valid block has always been determined,
     * except of course in cases where that block is air.
     */
    @SuppressWarnings("deprecation") // No good alternative in 1.12. Just ignoring this.
    private static Optional<IBlockState> _getBlock(String registryName, int meta) {
        final ResourceLocation location = new ResourceLocation(registryName);
        final Block block = ForgeRegistries.BLOCKS.getValue(location);

        // Ensure this value to be anything but air.
        if (Blocks.AIR.equals(block)) {
            return empty();
        } else if (block != null) {
            return full(block.getStateFromMeta(meta));
        }
        return empty();
    }
}