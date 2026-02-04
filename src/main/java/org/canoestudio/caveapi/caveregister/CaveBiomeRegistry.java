package org.canoestudio.caveapi.caveregister;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import org.canoestudio.caveapi.api.CaveBiome;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cave Biome Registry
 * Manages the registration and retrieval of cave biomes
 */
@Mod.EventBusSubscriber
public class CaveBiomeRegistry extends IForgeRegistryEntry.Impl<CaveBiome> {
    private static IForgeRegistry<CaveBiome> registry;
    private static final Map<ResourceLocation, CaveBiome> BIOME_MAP = new ConcurrentHashMap<>();
    private static final Map<String, CaveBiome> BIOME_NAME_MAP = new ConcurrentHashMap<>();
    private static boolean initialized = false;
    
    /**
     * Create and initialize the registry
     */
    public static void createRegistry() {
        if (initialized) {
            return;
        }
        
        RegistryBuilder<CaveBiome> registryBuilder = new RegistryBuilder<CaveBiome>();
        registryBuilder.setType(CaveBiome.class);
        registryBuilder.setName(new ResourceLocation("caveapi", "cave_biomes"));
        registryBuilder.setIDRange(0, 255);
        
        registry = registryBuilder.create();
        initialized = true;
    }
    
    /**
     * Get the registry instance
     * @return The cave biome registry
     */
    public static IForgeRegistry<CaveBiome> getRegistry() {
        if (!initialized) {
            createRegistry();
        }
        return registry;
    }
    
    /**
     * Register a cave biome
     * @param biome The cave biome to register
     */
    public static void register(CaveBiome biome) {
        if (!initialized) {
            createRegistry();
        }
        registry.register(biome);
        // Manually add to maps since IBuilderCallback is not available in 1.12.2
        BIOME_MAP.put(biome.getRegistryName(), biome);
        BIOME_NAME_MAP.put(biome.getName().toLowerCase(), biome);
    }
    
    /**
     * Get a cave biome by its registry name
     * @param name Registry name
     * @return The cave biome, or null if not found
     */
    public static CaveBiome getBiome(ResourceLocation name) {
        return BIOME_MAP.get(name);
    }
    
    /**
     * Get a cave biome by its display name
     * @param name Display name
     * @return The cave biome, or null if not found
     */
    public static CaveBiome getBiome(String name) {
        return BIOME_NAME_MAP.get(name.toLowerCase());
    }
    
    /**
     * Get all registered cave biomes
     * @return Map of registry names to cave biomes
     */
    public static Map<ResourceLocation, CaveBiome> getAllBiomes() {
        return new HashMap<>(BIOME_MAP);
    }
    
    /**
     * Select a random cave biome based on weights
     * @param random Random number generator
     * @param y Y coordinate to check biome height range
     * @return A random cave biome, or null if none available
     */
    public static CaveBiome getRandomBiome(Random random, int y) {
        if (BIOME_MAP.isEmpty()) {
            return null;
        }
        
        // Calculate total weight of biomes available at this y level
        int totalWeight = 0;
        for (CaveBiome biome : BIOME_MAP.values()) {
            if (y >= biome.getMinY() && y <= biome.getMaxY()) {
                totalWeight += biome.getWeight();
            }
        }
        
        if (totalWeight <= 0) {
            return null;
        }
        
        // Select a random biome based on weight
        int randomWeight = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (CaveBiome biome : BIOME_MAP.values()) {
            if (y >= biome.getMinY() && y <= biome.getMaxY()) {
                currentWeight += biome.getWeight();
                if (randomWeight < currentWeight) {
                    return biome;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Event handler for registry registration
     * @param event Registry event
     */
    @SubscribeEvent
    public static void onRegisterBiomes(RegistryEvent.Register<CaveBiome> event) {
        createRegistry();
        
        // Register default cave biomes
        registerDefaultBiomes(event.getRegistry());
    }
    
    /**
     * Register default cave biomes
     * @param registry The registry to register to
     */
    private static void registerDefaultBiomes(IForgeRegistry<CaveBiome> registry) {
        // Register Stone Cave biome
        CaveBiome stoneCave = new CaveBiome.Builder(
                new ResourceLocation("caveapi", "stone_cave"),
                "Stone Cave",
                100
        )
        .setFloorBlock(Blocks.STONE)
        .setWallBlock(Blocks.STONE)
        .setCeilingBlock(Blocks.STONE)
        .setAmbientLight(0.1f)
        .setMinY(1)
        .setMaxY(128)
        .build();
        stoneCave.setRegistryName(stoneCave.getRegistryName());
        registry.register(stoneCave);
        BIOME_MAP.put(stoneCave.getRegistryName(), stoneCave);
        BIOME_NAME_MAP.put(stoneCave.getName().toLowerCase(), stoneCave);
        
        // Register Dirt Cave biome
        CaveBiome dirtCave = new CaveBiome.Builder(
                new ResourceLocation("caveapi", "dirt_cave"),
                "Dirt Cave",
                50
        )
        .setFloorBlock(Blocks.DIRT)
        .setWallBlock(Blocks.STONE)
        .setCeilingBlock(Blocks.STONE)
        .setAmbientLight(0.15f)
        .setHasWater(true)
        .setHasVegetation(true)
        .setMinY(32)
        .setMaxY(64)
        .build();
        dirtCave.setRegistryName(dirtCave.getRegistryName());
        registry.register(dirtCave);
        BIOME_MAP.put(dirtCave.getRegistryName(), dirtCave);
        BIOME_NAME_MAP.put(dirtCave.getName().toLowerCase(), dirtCave);
        
        // Register Lava Cave biome
        CaveBiome lavaCave = new CaveBiome.Builder(
                new ResourceLocation("caveapi", "lava_cave"),
                "Lava Cave",
                30
        )
        .setFloorBlock(Blocks.NETHERRACK)
        .setWallBlock(Blocks.NETHERRACK)
        .setCeilingBlock(Blocks.NETHERRACK)
        .setAmbientLight(0.3f)
        .setHasLava(true)
        .setMinY(1)
        .setMaxY(32)
        .build();
        lavaCave.setRegistryName(lavaCave.getRegistryName());
        registry.register(lavaCave);
        BIOME_MAP.put(lavaCave.getRegistryName(), lavaCave);
        BIOME_NAME_MAP.put(lavaCave.getName().toLowerCase(), lavaCave);
        
        // Register Ice Cave biome
        CaveBiome iceCave = new CaveBiome.Builder(
                new ResourceLocation("caveapi", "ice_cave"),
                "Ice Cave",
                20
        )
        .setFloorBlock(Blocks.SNOW)
        .setWallBlock(Blocks.ICE)
        .setCeilingBlock(Blocks.ICE)
        .setAmbientLight(0.2f)
        .setHasWater(true)
        .setMinY(64)
        .setMaxY(128)
        .build();
        iceCave.setRegistryName(iceCave.getRegistryName());
        registry.register(iceCave);
        BIOME_MAP.put(iceCave.getRegistryName(), iceCave);
        BIOME_NAME_MAP.put(iceCave.getName().toLowerCase(), iceCave);
    }
    
    /**
     * Get the number of registered biomes
     * @return Number of registered biomes
     */
    public static int getBiomeCount() {
        return BIOME_MAP.size();
    }
    
    /**
     * Clear all registered biomes (for testing purposes)
     */
    public static void clear() {
        BIOME_MAP.clear();
        BIOME_NAME_MAP.clear();
        initialized = false;
    }
}