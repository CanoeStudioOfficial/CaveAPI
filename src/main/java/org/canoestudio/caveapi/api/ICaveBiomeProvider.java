package org.canoestudio.caveapi.api;

import net.minecraft.world.World;

/**
 * 3D Cave Biome Provider Interface
 * Defines a system for retrieving cave biomes in 3D space (X, Y, Z)
 */
public interface ICaveBiomeProvider {
    /**
     * Get the cave biome at the specified 3D coordinates
     * @param world The world instance
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return The cave biome at this location
     */
    CaveBiome getBiome(World world, int x, int y, int z);

    /**
     * Initialize the provider with a seed
     * @param seed The world seed or specific provider seed
     */
    void initialize(long seed);
}
