package caveapi.api;

import caveapi.api.CaveGenerator;
import caveapi.world.CaveGeneratorManager;

import java.util.*;

public class CaveRegistry {
    private final CaveGeneratorManager manager;

    public CaveRegistry(CaveGeneratorManager manager) {
        this.manager = manager;
    }

    /**
     * Register a cave generator for a specified biome
     *
     * @param biomeNames  list of biomes (registration format: modid:biome_name)
     * @param generator custom cave generator implementation
     */
    public void registerGenerator(String[] biomeNames, CaveGenerator generator) {
        manager.registerGenerator(biomeNames, generator);
    }

    /**
     * Register a conditional backup generator (effective when specific neighboring biomes are near the target biome)
     *
     * @param targetBiome   the main biome requiring a backup rule
     * @param neighborBiomes the list of nearby biomes needed to trigger the condition
     * @param generator     the backup cave generator
     */
    public void registerFallbackRule(String targetBiome, List<String> neighborBiomes, CaveGenerator generator) {
        manager.addFallbackRule(targetBiome, neighborBiomes, generator);
    }
}