package com.personthecat.cavegenerator.data;

import com.personthecat.cavegenerator.config.CavePreset;
import com.personthecat.cavegenerator.model.ArchaeneWallFixed;
import com.personthecat.cavegenerator.model.BlockCheck;
import com.personthecat.cavegenerator.model.Range;
import com.personthecat.cavegenerator.util.HjsonMapper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.hjson.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.personthecat.cavegenerator.util.CommonMethods.empty;
import static com.personthecat.cavegenerator.util.CommonMethods.full;

@Builder
@FieldNameConstants
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class CavernSettings {

    /** The name of this feature to be used globally in serialization. */
    private static final String FEATURE_NAME = CavePreset.Fields.caverns;

    /** The default noise generator settings used by the caverns feature. */
    private static final NoiseSettings DEFAULT_GENERATOR =
        NoiseSettings.builder().frequency(0.0143F).threshold(Range.of(-0.6F)).stretch(0.5F).octaves(1).build();

    /** The default ceiling noise parameters used by caverns, if absent. */
    private static final NoiseMapSettings DEFAULT_CEIL_NOISE =
        NoiseMapSettings.builder().frequency(0.02F).range(Range.of(-17, -3)).build();

    /** The default floor noise parameters used by caverns, if absent. */
    private static final NoiseMapSettings DEFAULT_FLOOR_NOISE =
        NoiseMapSettings.builder().frequency(0.02F).range(Range.of(0, 8)).build();

    private static final NoiseMapSettings DEFAULT_HEIGHT_OFFSET =
        NoiseMapSettings.builder().frequency(0.005F).range(Range.of(0, 50)).build();

    /** The default wall noise used at biome borders. */
    private static final NoiseMapSettings DEFAULT_WALL_NOISE =
        NoiseMapSettings.builder().frequency(0.02F).range(Range.of(9, 15)).build();

    /** Transformations for biome walls for this generator. */
    private static final NoiseMapSettings DEFAULT_WALL_OFFSET =
        NoiseMapSettings.builder().frequency(0.05F).range(Range.of(0, 255)).build();

    /** Default spawn conditions for all cavern generators. */
    private static final ConditionSettings DEFAULT_CONDITIONS = ConditionSettings.builder()
        .height(Range.of(10, 50)).ceiling(full(DEFAULT_CEIL_NOISE)).floor(full(DEFAULT_FLOOR_NOISE)).build();

    /** Default decorator settings for all caverns generators. */
    private static final DecoratorSettings DEFAULT_DECORATORS = DecoratorSettings.DEFAULTS;

    /** Conditions for these caverns to spawn. */
    @Default ConditionSettings conditions = DEFAULT_CONDITIONS;

    /** Cave blocks and wall decorators applied to these caverns. */
    @Default DecoratorSettings decorators = DEFAULT_DECORATORS;

    /** The number of blocks to iterate and interpolate between when generating. */
    @Default int resolution = 1;

    /** How much to offset the y-value input to the generator based on (x, z). */
    @Default Optional<NoiseMapSettings> offset = empty();

    /** Settings for how to generate these walls, if applicable. */
    @Default Optional<NoiseMapSettings> walls = empty();

    /** Settings for translating wall noise up and down to obscure repetition. */
    @Default Optional<NoiseMapSettings> wallOffset = empty();

    /** Modifies the ceiling and floor curve around biome borders. */
    @Default float wallCurveRatio = 1.0f;

    /** Whether to interpolate biome borders for smoother walls. */
    @Default boolean wallInterpolation = false;

    /** A list of noise generators to produce the shape of these caverns. */
    @Default List<NoiseSettings> generators = Collections.singletonList(DEFAULT_GENERATOR);

    /** BlueFire's magic voodoo hoodoo for configurable cavern height-based threshold settings.  */
    @Default List<Float> archaeneHeight = Collections.emptyList();

    /** BlueFire's magic voodoo hoodoo for configurable cavern horizontal threshold settings.  */
    @Default List<Float> archaeneWalls = Collections.emptyList();

    /** BlueFire's magic voodoo hoodoo for configuring how the horizontal threshold settings are applied  */
    @Default List<Integer> archaeneWallArray = Collections.emptyList();

    /** BlueFire's magic voodoo hoodoo for ... castle walls.  */
    @Default List<ArchaeneWallFixed> archaeneWallFixed = Collections.emptyList();

    /** Whether to interpolate biome borders for smoother walls. */
    @Default boolean archaeneWallOnly = false;

    /** Whether to interpolate cavern noise. */
    @Default boolean archaeneInterpolation = false;

    /** Attempt to slur sampled caverns upward by adding a portion of the next sample down if it's higher. */
    @Default float archaeneInterpStretch = 0.0f;

    /** Interpolation X, Y, and Z resolution. */
    @Default int archaeneInterpX = 3;
    @Default int archaeneInterpY = 2;
    @Default int archaeneInterpZ = 3;

    /** Whether to use alternate generator that skips all noise and does not place blocks, only placing decorations. */
    @Default boolean archaeneDecorator = false;

    /** A list of tunnels that will spawn connected to these caverns. */
    @Default Optional<TunnelSettings> branches = empty();

    /** Whether to run this generator as a late feature. Will NOT be removed. */
    @Default boolean deferred = false;

    public static CavernSettings from(JsonObject json, OverrideSettings overrides) {
        final ConditionSettings conditions = overrides.apply(DEFAULT_CONDITIONS.toBuilder()).build();
        final DecoratorSettings decorators = overrides.apply(DEFAULT_DECORATORS.toBuilder()).build();
        return copyInto(json, builder().conditions(conditions).decorators(decorators));
    }

    public static CavernSettings from(JsonObject json) {
        return copyInto(json, builder());
    }

    private static CavernSettings copyInto(JsonObject json, CavernSettingsBuilder builder) {
        final CavernSettings original = builder.build();
        return new HjsonMapper(json)
            .mapSelf(o -> builder.conditions(ConditionSettings.from(o, original.conditions)))
            .mapSelf(o -> builder.decorators(DecoratorSettings.from(o, original.decorators)))
            .mapInt(Fields.resolution, builder::resolution)
            .mapObject(Fields.offset, o -> builder.offset(full(NoiseMapSettings.from(o, DEFAULT_HEIGHT_OFFSET))))
            .mapObject(Fields.walls, o -> builder.walls(full(NoiseMapSettings.from(o, DEFAULT_WALL_NOISE))))
            .mapObject(Fields.wallOffset, o -> builder.wallOffset(full(NoiseMapSettings.from(o, DEFAULT_WALL_OFFSET))))
            .mapFloat(Fields.wallCurveRatio, builder::wallCurveRatio)
            .mapBool(Fields.wallInterpolation, builder::wallInterpolation)
            .mapArray(Fields.generators, CavernSettings::createNoise, builder::generators)
            .mapFloatList(Fields.archaeneHeight, builder::archaeneHeight)
            .mapFloatList(Fields.archaeneWalls, builder::archaeneWalls)
            .mapIntList(Fields.archaeneWallArray, builder::archaeneWallArray)
            .mapValueArray(CavernSettings.Fields.archaeneWallFixed, ArchaeneWallFixed::fromValue, builder::archaeneWallFixed)
            .mapBool(Fields.archaeneWallOnly, builder::archaeneWallOnly)
            .mapBool(Fields.archaeneInterpolation, builder::archaeneInterpolation)
            .mapFloat(Fields.archaeneInterpStretch, builder::archaeneInterpStretch)
            .mapInt(Fields.archaeneInterpX, builder::archaeneInterpX)
            .mapInt(Fields.archaeneInterpY, builder::archaeneInterpY)
            .mapInt(Fields.archaeneInterpZ, builder::archaeneInterpZ)
            .mapBool(Fields.archaeneDecorator, builder::archaeneDecorator)
            .mapObject(Fields.branches, o -> copyBranches(builder, o))
            .mapBool(Fields.deferred, builder::deferred)
            .release(builder::build);
    }

    private static NoiseSettings createNoise(JsonObject json) {
        return NoiseSettings.from(json, DEFAULT_GENERATOR);
    }

    private static void copyBranches(CavernSettingsBuilder builder, JsonObject branches) {
        // Includes overrides and settings from the caverns object.
        final CavernSettings updated = builder.build();
        builder.branches(full(TunnelSettings.from(branches, updated.conditions, updated.decorators)));
    }
}
