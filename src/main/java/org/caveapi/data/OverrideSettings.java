package org.caveapi.data;

import org.caveapi.config.CavePreset;
import org.caveapi.config.FieldHistory;
import org.caveapi.model.Range;
import org.caveapi.util.HjsonMapper;
import org.caveapi.util.HjsonTools;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.ArrayUtils;
import org.hjson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.caveapi.util.CommonMethods.empty;
import static org.caveapi.util.CommonMethods.full;

/** Any settings that can be written at the top level to serve as default values. */
@Builder
@FieldNameConstants
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class OverrideSettings {

    /** Whether the biomes list should be treated as a blacklist. */
    @Default Optional<Boolean> blacklistBiomes = empty();
    /** If not 0, this dimension's biome provider is checked instead of the current dimension. */
    @Default Optional<Integer> proxyDimension = empty();
    /** A list of biomes in which this feature can spawn. */
    @Default Optional<List<Biome>> biomes = empty();

    /** Whether the biomes list should be treated as a blacklist. */
    @Default Optional<Boolean> blacklistBiomes2 = empty();
    /** If not 0, this dimension's biome provider is checked instead of the current dimension. */
    @Default Optional<Integer> proxyDimension2 = empty();
    /** A list of biomes in which this feature can spawn. */
    @Default Optional<List<Biome>> biomes2 = empty();

    /** Whether the biomes list should be treated as a blacklist. */
    @Default Optional<Boolean> blacklistBiomes3 = empty();
    /** If not 0, this dimension's biome provider is checked instead of the current dimension. */
    @Default Optional<Integer> proxyDimension3 = empty();
    /** A list of biomes in which this feature can spawn. */
    @Default Optional<List<Biome>> biomes3 = empty();

    /** Whether the biomes list should be treated as a blacklist. */
    @Default Optional<Boolean> blacklistBiomes4 = empty();
    /** If not 0, this dimension's biome provider is checked instead of the current dimension. */
    @Default Optional<Integer> proxyDimension4 = empty();
    /** A list of biomes in which this feature can spawn. */
    @Default Optional<List<Biome>> biomes4 = empty();

    /** Whether the dimension list should be treated as a blacklist. */
    @Default Optional<Boolean> blacklistDimensions = empty();

    /** A list of dimensions in which this feature can spawn.. */
    @Default Optional<List<Integer>> dimensions = empty();

    /** Height restrictions for the current feature. */
    @Default Optional<Range> height = empty();

    /** Settings used for augmenting the maximum height level. */
    @Default Optional<NoiseMapSettings> floor = empty();

    /** Settings used for augmenting the minimum height level. */
    @Default Optional<NoiseMapSettings> ceiling = empty();

    /** Settings used to determine whether this feature can spawn when given 2 coordinates. */
    @Default Optional<NoiseRegionSettings> region = empty();

    /** Settings used to control 3-dimensional placement of this feature. */
    @Default Optional<NoiseSettings> noise = empty();

    /** All of the blocks which can be replaced by this decorator. */
    @Default Optional<List<IBlockState>> replaceableBlocks = empty();

    /** Whether to include the blocks from various other features in this list. */
    @Default Optional<Boolean> replaceDecorators = empty();

    /** A list of blocks for this carver to place instead of air. */
    @Default Optional<List<CaveBlockSettings>> caveBlocks = empty();

    /** A list of blocks to replace the walls of this carver with. */
    @Default Optional<List<WallDecoratorSettings>> wallDecorators = empty();

    /** A variant of wall decorators which can spawn multiple levels deep in the ground only. */
    @Default Optional<List<PondSettings>> ponds = empty();

    /** Variant of wall decorators which can spawn multiple levels deep without directionality. */
    @Default Optional<ShellSettings> shell = empty();

    /** Optional branch overrides for all tunnels. */
    @Default Optional<TunnelSettings> branches = empty();

    /** Optional room overrides for all tunnels. For backwards compatibility. */
    @Default Optional<RoomSettings> rooms = empty();

    /** An internal-only list of decorator blocks at <em>every level</em>. */
    @Default List<IBlockState> globalDecorators = Collections.emptyList();

    public static OverrideSettings from(JsonObject json) {
        final OverrideSettingsBuilder builder = builder().globalDecorators(getAllDecorators(json));
        return new HjsonMapper(json)
            .mapBool(Fields.blacklistBiomes, b -> builder.blacklistBiomes(full(b)))
            .mapInt(Fields.proxyDimension, p -> builder.proxyDimension(full(p)))
            .mapBiomes(Fields.biomes, l -> builder.biomes(full(l)))
            .mapBool(Fields.blacklistBiomes2, b -> builder.blacklistBiomes2(full(b)))
            .mapInt(Fields.proxyDimension2, p -> builder.proxyDimension2(full(p)))
            .mapBiomes(Fields.biomes2, l -> builder.biomes2(full(l)))
            .mapBool(Fields.blacklistBiomes3, b -> builder.blacklistBiomes3(full(b)))
            .mapInt(Fields.proxyDimension3, p -> builder.proxyDimension3(full(p)))
            .mapBiomes(Fields.biomes3, l -> builder.biomes3(full(l)))
            .mapBool(Fields.blacklistBiomes4, b -> builder.blacklistBiomes4(full(b)))
            .mapInt(Fields.proxyDimension4, p -> builder.proxyDimension4(full(p)))
            .mapBiomes(Fields.biomes4, l -> builder.biomes4(full(l)))
            .mapBool(Fields.blacklistDimensions, b -> builder.blacklistBiomes(full(b)))
            .mapIntList(Fields.dimensions, l -> builder.dimensions(full(l)))
            .mapRange(Fields.height, r -> builder.height(full(r)))
            .mapObject(Fields.floor, o -> builder.floor(full(NoiseMapSettings.from(o))))
            .mapObject(Fields.ceiling, o -> builder.ceiling(full(NoiseMapSettings.from(o))))
            .mapObject(Fields.noise, o -> builder.noise(full(NoiseSettings.from(o))))
            .mapStateList(Fields.replaceableBlocks, l -> builder.replaceableBlocks(full(l)))
            .mapBool(Fields.replaceDecorators, b -> builder.replaceDecorators(full(b)))
            .mapArray(Fields.caveBlocks, CaveBlockSettings::from, l -> builder.caveBlocks(full(l)))
            .mapArray(Fields.wallDecorators, WallDecoratorSettings::from, l -> builder.wallDecorators(full(l)))
            .mapArray(Fields.ponds, PondSettings::from, l -> builder.ponds(full(l)))
            .mapObject(Fields.shell, s -> builder.shell(full(ShellSettings.from(s))))
            .mapObject(Fields.branches, b -> builder.branches(full(TunnelSettings.from(b))))
            .mapObject(Fields.rooms, r -> builder.rooms(full(RoomSettings.from(r))))
            .release(builder::build);
    }

    public ConditionSettings.ConditionSettingsBuilder apply(ConditionSettings.ConditionSettingsBuilder builder) {
        this.blacklistBiomes.ifPresent(builder::blacklistBiomes);
        this.proxyDimension.ifPresent(builder::proxyDimension);
        this.biomes.ifPresent(builder::biomes);
        this.blacklistBiomes2.ifPresent(builder::blacklistBiomes2);
        this.proxyDimension2.ifPresent(builder::proxyDimension2);
        this.biomes2.ifPresent(builder::biomes2);
        this.blacklistBiomes3.ifPresent(builder::blacklistBiomes3);
        this.proxyDimension3.ifPresent(builder::proxyDimension3);
        this.biomes3.ifPresent(builder::biomes3);
        this.blacklistBiomes4.ifPresent(builder::blacklistBiomes4);
        this.proxyDimension4.ifPresent(builder::proxyDimension4);
        this.biomes4.ifPresent(builder::biomes4);
        this.blacklistDimensions.ifPresent(builder::blacklistDimensions);
        this.dimensions.ifPresent(builder::dimensions);
        this.height.ifPresent(builder::height);
        this.floor.ifPresent(n -> builder.floor(full(n)));
        this.ceiling.ifPresent(n -> builder.ceiling(full(n)));
        this.region.ifPresent(n -> builder.region(full(n)));
        this.noise.ifPresent(n -> builder.noise(full(n)));
        return builder;
    }

    public DecoratorSettings.DecoratorSettingsBuilder apply(DecoratorSettings.DecoratorSettingsBuilder builder) {
        builder.globalDecorators(this.globalDecorators);
        this.replaceableBlocks.ifPresent(builder::replaceableBlocks);
        this.replaceDecorators.ifPresent(builder::replaceDecorators);
        this.caveBlocks.ifPresent(builder::caveBlocks);
        this.wallDecorators.ifPresent(builder::wallDecorators);
        this.ponds.ifPresent(builder::ponds);
        this.shell.ifPresent(builder::shell);
        return builder;
    }

    public TunnelSettings.TunnelSettingsBuilder apply(TunnelSettings.TunnelSettingsBuilder builder) {
        this.branches.ifPresent(b -> builder.branches(full(b)));
        this.rooms.ifPresent(r -> builder.rooms(full(r)));
        return builder;
    }

    private static List<IBlockState> getAllDecorators(JsonObject json) {
        final List<IBlockState> decorators = new ArrayList<>();
        addAllDecorators(decorators, json); // Top-level overrides
        addAllDecorators(decorators, json, Fields.branches);
        addAllDecorators(decorators, json, Fields.rooms);
        addAllDecorators(decorators, json, CavePreset.Fields.tunnels);
        addAllDecorators(decorators, json, CavePreset.Fields.tunnels, TunnelSettings.Fields.branches);
        addAllDecorators(decorators, json, CavePreset.Fields.ravines);
        addAllDecorators(decorators, json, CavePreset.Fields.caverns);
        addAll(decorators, json, ClusterSettings.Fields.states, CavePreset.Fields.clusters);
        addAll(decorators, json, LayerSettings.Fields.state, CavePreset.Fields.layers);
        return decorators;
    }

    private static void addAllDecorators(List<IBlockState> decorators, JsonObject json, String... path) {
        FieldHistory.withPath(ArrayUtils.add(path, DecoratorSettings.Fields.wallDecorators))
            .forEach(json, j -> addAll(decorators, j, WallDecoratorSettings.Fields.states));
        FieldHistory.withPath(ArrayUtils.add(path, DecoratorSettings.Fields.caveBlocks))
            .forEach(json, j -> addAll(decorators, j, CaveBlockSettings.Fields.states));
        FieldHistory.withPath(ArrayUtils.add(path, DecoratorSettings.Fields.ponds))
            .forEach(json, j -> addAll(decorators, j, PondSettings.Fields.states));
        FieldHistory.withPath(ArrayUtils.addAll(path, DecoratorSettings.Fields.shell, ShellSettings.Fields.decorators))
            .forEach(json, j -> addAll(decorators, j, ShellSettings.Decorator.Fields.states));
    }

    private static void addAll(List<IBlockState> decorators, JsonObject json, String field, String... path) {
        FieldHistory.withPath(path).forEach(json, j ->
            HjsonTools.getStateList(j, field).ifPresent(decorators::addAll));
    }

}
