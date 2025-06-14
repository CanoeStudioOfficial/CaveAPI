package caveapi.cavegenerator.model;

import caveapi.cavegenerator.data.ConditionSettings;
import caveapi.cavegenerator.noise.DummyGenerator;
import fastnoise.FastNoise;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class Conditions {

    /** Any conditions for spawning this feature according to the current biome. */
    @Default Predicate<Biome> biomes = b -> true;

    /** Indicates whether this feature has specific biome restrictions. */
    @Default boolean hasBiomes = false;

    /** Indicates whether this feature has noise-based region restrictions. */
    @Default boolean hasRegion = false;

    /** Any conditions for spawning this feature according to the current dimension. */
    @Default Predicate<Integer> dimensions = d -> true;

    /** Height restrictions for the current feature. */
    @Default Range height = Range.of(0, 255);

    /** The value produced by this generator will augment the maximum height level. */
    @Default FastNoise floor = new DummyGenerator(0F);

    /** The value produced by this generator will augment the minimum height level. */
    @Default FastNoise ceiling = new DummyGenerator(0F);

    /** 2-dimensional noise constraints for this feature. */
    @Default FastNoise region = new DummyGenerator(0F);

    /** 3-dimensional noise constraints for this feature. */
    @Default FastNoise noise = new DummyGenerator(0F);

    public static Conditions compile(ConditionSettings settings, World world) {
        final ConditionsBuilder builder = builder()
            .hasBiomes(settings.blacklistBiomes || !settings.biomes.isEmpty())
            .hasRegion(settings.region.isPresent())
            .biomes(compileBiomes(settings))
            .dimensions(compileDimensions(settings))
            .height(settings.height);

        settings.floor.ifPresent(c -> builder.floor(c.getGenerator(world)));
        settings.ceiling.ifPresent(c -> builder.ceiling(c.getGenerator(world)));
        settings.region.ifPresent(c -> builder.region(c.getGenerator(world)));
        settings.noise.ifPresent(c -> builder.noise(c.getGenerator(world)));
        return builder.build();
    }

    private static Predicate<Biome> compileBiomes(ConditionSettings settings) {
        final List<Biome> list = settings.biomes;
        if (list.isEmpty()) {
            return b -> true;
        } else if (list.size() == 1) {
            final Biome listed = list.get(0);
            return settings.blacklistBiomes ? b -> !listed.equals(b) : listed::equals;
        }
        final List<Biome> nonRedundant = Collections.unmodifiableList(new ArrayList<>(new HashSet<>(list)));
        return settings.blacklistBiomes ? b -> !nonRedundant.contains(b) : nonRedundant::contains;
    }

    private static Predicate<Integer> compileDimensions(ConditionSettings settings) {
        final List<Integer> list = settings.dimensions;
        if (list.isEmpty()) {
            return d -> true;
        } else if (list.size() == 1) {
            final int listed = list.get(0);
            return settings.blacklistDimensions ? d -> listed != d : d -> listed == d;
        }
        final IntList nonRedundant = IntLists.unmodifiable(new IntArrayList(new HashSet<>(list)));
        return settings.blacklistDimensions ? d -> !nonRedundant.contains(d) : nonRedundant::contains;
    }

    /** Get the current height range when given two absolute coordinates. */
    public Range getColumn(int x, int z) {
        final int min = height.min + (int) floor.GetAdjustedNoise((float) x, (float) z);
        final int max = height.max + (int) ceiling.GetAdjustedNoise((float) x, (float) z);
        return Range.checkedOrEmpty(min, max);
    }

    /** Get the current height range when given two absolute coordinates and a heightmap of the current chunk. */
    public Range getColumn(int[][] heightmap, int x, int z) {
        final int min = height.min + (int) floor.GetAdjustedNoise((float) x, (float) z);
        final int max = Math.min(height.max, heightmap[x & 15][z & 15]) + (int) ceiling.GetAdjustedNoise((float) x, (float) z);
        return Range.checkedOrEmpty(min, max);
    }
}
