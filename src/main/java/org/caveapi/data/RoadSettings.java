package com.personthecat.cavegenerator.data;

import com.personthecat.cavegenerator.config.CavePreset;
import com.personthecat.cavegenerator.model.BlockCheck;
import com.personthecat.cavegenerator.model.Direction;
import com.personthecat.cavegenerator.model.Range;
import com.personthecat.cavegenerator.util.HjsonMapper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import org.hjson.JsonObject;

import java.util.Collections;
import java.util.List;

@Builder
@FieldNameConstants
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class RoadSettings {

    /** The name of this feature to be used globally in serialization. */
    private static final String FEATURE_NAME = CavePreset.Fields.roads;

    /** Default spawn conditions for all structure generators. */
    private static final ConditionSettings DEFAULT_CONDITIONS = ConditionSettings.builder()
        .height(Range.of(10, 50)).build();

    /** Conditions for these tunnels to spawn. */
    @Default ConditionSettings conditions = DEFAULT_CONDITIONS;

    /** A list of source blocks that this road can spawn on top of. */
    @Default List<IBlockState> matchers = Collections.singletonList(Blocks.STONE.getDefaultState());

    /** The 0-1 chance that any spawn attempt is successful, if *also* valid. */
    @Default float chance = 1.0F;

    public static RoadSettings from(JsonObject json, OverrideSettings overrides) {
        final ConditionSettings conditions = overrides.apply(DEFAULT_CONDITIONS.toBuilder()).build();
        return copyInto(json, builder().conditions(conditions));
    }

    public static RoadSettings from(JsonObject json) {
        return copyInto(json, builder());
    }

    private static RoadSettings copyInto(JsonObject json, RoadSettings.RoadSettingsBuilder builder) {
        final RoadSettings original = builder.build();
        return new HjsonMapper(json)
            .mapSelf(o -> builder.conditions(ConditionSettings.from(o, original.conditions)))
            .release(builder::build);
    }

}
