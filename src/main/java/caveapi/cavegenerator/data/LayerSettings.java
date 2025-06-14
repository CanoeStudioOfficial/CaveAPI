package caveapi.cavegenerator.data;

import caveapi.cavegenerator.config.CavePreset;
import caveapi.cavegenerator.model.Range;
import caveapi.cavegenerator.util.HjsonMapper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import org.hjson.JsonObject;

import java.util.Collections;
import java.util.List;

import static caveapi.cavegenerator.util.CommonMethods.full;

@Builder
@FieldNameConstants
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class LayerSettings {

    /** The name of this feature to be used globally in serialization. */
    private static final String FEATURE_NAME = CavePreset.Fields.layers;

    /** The default noise values used for all layers. */
    private static final NoiseMapSettings DEFAULT_NOISE = NoiseMapSettings.builder()
        .frequency(0.015f).range(Range.of(-7, 7)).build();

    /** Default spawn conditions for all layer generators. */
    private static final ConditionSettings DEFAULT_CONDITIONS = ConditionSettings.builder()
        .height(Range.of(0, 20)).ceiling(full(DEFAULT_NOISE)).build();

    /** A list of blocks in which this feature can spawn. */
    @Default List<IBlockState> matchers = Collections.singletonList(Blocks.STONE.getDefaultState());

    /** Conditions for these layers to spawn. */
    @Default ConditionSettings conditions = DEFAULT_CONDITIONS;

    /** The block to spawn as a stone "layer" underground. */
    IBlockState state;

    public static LayerSettings from(JsonObject json, OverrideSettings overrides) {
        final ConditionSettings conditions = overrides.apply(DEFAULT_CONDITIONS.toBuilder()).build();
        return copyInto(json, builder().conditions(conditions));
    }

    public static LayerSettings from(JsonObject json) {
        return copyInto(json, builder());
    }
        
    private static LayerSettings copyInto(JsonObject json, LayerSettingsBuilder builder) {
        final LayerSettings original = builder.build();
        return new HjsonMapper(json)
            .mapRequiredState(Fields.state, FEATURE_NAME, builder::state)
            .mapSelf(o -> builder.conditions(ConditionSettings.from(o, original.conditions)))
            .mapStateList(Fields.matchers, builder::matchers)
            .release(builder::build);
    }

}
