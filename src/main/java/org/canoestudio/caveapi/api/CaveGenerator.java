package org.canoestudio.caveapi.api;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * Minecraft 1.12.2 Universal Cave Generator Interface
 * Provides modular cave generation functionality with custom configuration and event listening support
 */
public class CaveGenerator {
    private static EventBus eventBus;
    
    static {
        eventBus = new EventBus();
    }
    
    /**
     * Get the event bus
     * @return Event bus instance
     */
    public static EventBus getEventBus() {
        return eventBus;
    }
    
    /**
     * Register a cave generator to the event bus
     * @param generator The cave generator to register
     */
    public static void registerGenerator(ICaveGenerator generator) {
        eventBus.register(generator);
    }
    
    /**
     * Unregister a cave generator from the event bus
     * @param generator The cave generator to unregister
     */
    public static void unregisterGenerator(ICaveGenerator generator) {
        eventBus.unregister(generator);
    }
    
    // ========================== Cave Generation Events ==========================
    
    /**
     * Base class for cave generation events
     */
    public static abstract class CaveGenerationEvent {
        public final World world;
        public final Random random;
        public final ChunkPrimer chunkPrimer;
        public final int chunkX;
        public final int chunkZ;
        
        protected CaveGenerationEvent(World world, Random random, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
            this.world = world;
            this.random = random;
            this.chunkPrimer = chunkPrimer;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }
        
        /**
         * Event fired before cave generation
         */
        public static class Pre extends CaveGenerationEvent {
            public final double x;
            public final double y;
            public final double z;
            
            public Pre(World world, Random random, ChunkPrimer chunkPrimer, int chunkX, int chunkZ, double x, double y, double z) {
                super(world, random, chunkPrimer, chunkX, chunkZ);
                this.x = x;
                this.y = y;
                this.z = z;
            }
        }
        
        /**
         * Event fired after cave generation
         */
        public static class Post extends CaveGenerationEvent {
            public Post(World world, Random random, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
                super(world, random, chunkPrimer, chunkX, chunkZ);
            }
        }
    }
    
    // ========================== Universal Cave Generator Interface ==========================
    
    /**
     * Cave generator interface
     */
    public interface ICaveGenerator {
        
        /**
         * Get the unique identifier for this cave generator
         * @return Generator ID
         */
        String getGeneratorId();
        
        /**
         * Generate caves
         * @param world World object
         * @param random Random number generator
         * @param chunkPrimer Chunk primer
         * @param chunkX Chunk X coordinate
         * @param chunkZ Chunk Z coordinate
         * @param x Cave starting X coordinate
         * @param y Cave starting Y coordinate
         * @param z Cave starting Z coordinate
         */
        void generateCave(World world, Random random, ChunkPrimer chunkPrimer, int chunkX, int chunkZ, double x, double y, double z);
        
        /**
         * Check if a cave should be generated at this location
         * @param world World object
         * @param random Random number generator
         * @param chunkX Chunk X coordinate
         * @param chunkZ Chunk Z coordinate
         * @param y Cave Y coordinate
         * @return Whether a cave should be generated
         */
        boolean shouldGenerate(World world, Random random, int chunkX, int chunkZ, int y);
        
        /**
         * Get the generation priority
         * @return Priority, lower values mean higher priority
         */
        default int getPriority() {
            return 0;
        }
        
        /**
         * Get the generation chance
         * @return Generation chance (0.0 - 1.0)
         */
        default double getGenerationChance() {
            return 0.5;
        }
        
        /**
         * Get the minimum generation height
         * @return Minimum generation height
         */
        default int getMinY() {
            return 1;
        }
        
        /**
         * Get the maximum generation height
         * @return Maximum generation height
         */
        default int getMaxY() {
            return 128;
        }
        
        /**
         * Handle pre-generation event
         * @param event Pre-generation event
         */
        @SubscribeEvent
        default void onGenerationPre(CaveGenerationEvent.Pre event) {
            // Default implementation is empty, can be overridden by subclasses
        }
        
        /**
         * Handle post-generation event
         * @param event Post-generation event
         */
        @SubscribeEvent
        default void onGenerationPost(CaveGenerationEvent.Post event) {
            // Default implementation is empty, can be overridden by subclasses
        }
    }
    
    // ========================== Cave Configuration Class ==========================
    
    /**
     * Cave generation configuration
     */
    public static class CaveConfig {
        private String generatorId;
        private double generationChance;
        private int minY;
        private int maxY;
        private int priority;
        private boolean enabled;
        
        /**
         * Create a cave configuration
         * @param generatorId Generator ID
         */
        public CaveConfig(String generatorId) {
            this.generatorId = generatorId;
            this.generationChance = 0.5;
            this.minY = 1;
            this.maxY = 128;
            this.priority = 0;
            this.enabled = true;
        }
        
        /**
         * Get the generator ID
         * @return Generator ID
         */
        public String getGeneratorId() {
            return generatorId;
        }
        
        /**
         * Set the generation chance
         * @param chance Generation chance (0.0 - 1.0)
         * @return Configuration instance, supports method chaining
         */
        public CaveConfig setGenerationChance(double chance) {
            this.generationChance = Math.max(0.0, Math.min(1.0, chance));
            return this;
        }
        
        /**
         * Get the generation chance
         * @return Generation chance
         */
        public double getGenerationChance() {
            return generationChance;
        }
        
        /**
         * Set the minimum generation height
         * @param minY Minimum height
         * @return Configuration instance, supports method chaining
         */
        public CaveConfig setMinY(int minY) {
            this.minY = Math.max(1, minY);
            return this;
        }
        
        /**
         * Get the minimum generation height
         * @return Minimum height
         */
        public int getMinY() {
            return minY;
        }
        
        /**
         * Set the maximum generation height
         * @param maxY Maximum height
         * @return Configuration instance, supports method chaining
         */
        public CaveConfig setMaxY(int maxY) {
            this.maxY = Math.min(256, maxY);
            return this;
        }
        
        /**
         * Get the maximum generation height
         * @return Maximum height
         */
        public int getMaxY() {
            return maxY;
        }
        
        /**
         * Set the priority
         * @param priority Priority, lower values mean higher priority
         * @return Configuration instance, supports method chaining
         */
        public CaveConfig setPriority(int priority) {
            this.priority = priority;
            return this;
        }
        
        /**
         * Get the priority
         * @return Priority
         */
        public int getPriority() {
            return priority;
        }
        
        /**
         * Set whether the generator is enabled
         * @param enabled Whether to enable the generator
         * @return Configuration instance, supports method chaining
         */
        public CaveConfig setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }
        
        /**
         * Check if the generator is enabled
         * @return Whether the generator is enabled
         */
        public boolean isEnabled() {
            return enabled;
        }
    }
    
    // ========================== Abstract Cave Generator Base Class ==========================
    
    /**
     * Abstract cave generator base class
     * Provides basic implementation of the ICaveGenerator interface
     */
    public static abstract class AbstractCaveGenerator implements ICaveGenerator {
        protected final String generatorId;
        protected final CaveConfig config;
        
        /**
         * Create an abstract cave generator
         * @param generatorId Generator ID
         */
        public AbstractCaveGenerator(String generatorId) {
            this.generatorId = generatorId;
            this.config = new CaveConfig(generatorId);
        }
        
        /**
         * Create an abstract cave generator with custom configuration
         * @param generatorId Generator ID
         * @param config Cave configuration
         */
        public AbstractCaveGenerator(String generatorId, CaveConfig config) {
            this.generatorId = generatorId;
            this.config = config;
        }
        
        @Override
        public String getGeneratorId() {
            return generatorId;
        }
        
        @Override
        public boolean shouldGenerate(World world, Random random, int chunkX, int chunkZ, int y) {
            if (!config.isEnabled()) {
                return false;
            }
            
            if (y < config.getMinY() || y > config.getMaxY()) {
                return false;
            }
            
            return random.nextDouble() < config.getGenerationChance();
        }
        
        @Override
        public int getPriority() {
            return config.getPriority();
        }
        
        @Override
        public double getGenerationChance() {
            return config.getGenerationChance();
        }
        
        @Override
        public int getMinY() {
            return config.getMinY();
        }
        
        @Override
        public int getMaxY() {
            return config.getMaxY();
        }
        
        /**
         * Get the configuration
         * @return Cave configuration
         */
        public CaveConfig getConfig() {
            return config;
        }
    }
    
    // ========================== Cave Type Enumeration ==========================
    
    /**
     * Cave type enumeration
     */
    public enum CaveType {
        /** Normal cave */
        NORMAL,
        /** Large cave */
        LARGE,
        /** Canyon */
        CANYON,
        /** Crevice */
        CREVICE,
        /** Underwater cave */
        UNDERWATER,
        /** Lava cave */
        LAVA,
        /** Custom type */
        CUSTOM
    }
}
