package org.canoestudio.caveapi.caveregister;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.canoestudio.caveapi.api.CaveBiome;
import org.canoestudio.caveapi.api.ICaveBiomeProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Cave Biome Manager
 * Global manager for the 3D cave biome system
 */
@Mod.EventBusSubscriber
public class CaveBiomeManager {
    private static ICaveBiomeProvider activeProvider = new NoiseCaveBiomeProvider(64.0, 1.5);
    private static final Map<Integer, ICaveBiomeProvider> dimensionProviders = new HashMap<>();

    /**
     * Event handler for world loading
     * @param event World load event
     */
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            initialize(event.getWorld().getSeed());
        }
    }

    /**
     * Set the active biome provider for all dimensions
     * @param provider The provider to use
     */
    public static void setActiveProvider(ICaveBiomeProvider provider) {
        activeProvider = provider;
    }

    /**
     * Set a specific biome provider for a dimension
     * @param dimensionId Dimension ID
     * @param provider The provider to use
     */
    public static void setDimensionProvider(int dimensionId, ICaveBiomeProvider provider) {
        dimensionProviders.put(dimensionId, provider);
    }

    /**
     * Get the cave biome at the specified location
     * @param world The world instance
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return The 3D cave biome at this location
     */
    public static CaveBiome getBiome(World world, int x, int y, int z) {
        int dimensionId = world.provider.getDimension();
        ICaveBiomeProvider provider = dimensionProviders.getOrDefault(dimensionId, activeProvider);
        
        if (provider != null) {
            return provider.getBiome(world, x, y, z);
        }
        
        return null;
    }

    /**
     * Initialize the biome system for a specific seed
     * @param seed The seed to use
     */
    public static void initialize(long seed) {
        activeProvider.initialize(seed);
        for (ICaveBiomeProvider provider : dimensionProviders.values()) {
            provider.initialize(seed);
        }
    }
}
