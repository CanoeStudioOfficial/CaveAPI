package org.canoestudio.caveapi.api;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Random;

/**
 * Cave biome base class
 * Represents a specific type of cave environment with its own characteristics
 */
public class CaveBiome extends IForgeRegistryEntry.Impl<CaveBiome> {
    private final String name;
    private final int weight;
    private final Block floorBlock;
    private final Block wallBlock;
    private final Block ceilingBlock;
    private final float ambientLight;
    private final boolean hasWater;
    private final boolean hasLava;
    private final boolean hasVegetation;
    private final int minY;
    private final int maxY;
    
    /**
     * Create a new cave biome
     * @param registryName Unique registry name
     * @param name Display name
     * @param weight Spawn weight
     */
    public CaveBiome(ResourceLocation registryName, String name, int weight) {
        this.name = name;
        this.weight = weight;
        this.floorBlock = Blocks.STONE;
        this.wallBlock = Blocks.STONE;
        this.ceilingBlock = Blocks.STONE;
        this.ambientLight = 0.1f;
        this.hasWater = false;
        this.hasLava = false;
        this.hasVegetation = false;
        this.minY = 1;
        this.maxY = 128;
        this.setRegistryName(registryName);
    }
    
    /**
     * Get the display name
     * @return Display name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the spawn weight
     * @return Spawn weight
     */
    public int getWeight() {
        return weight;
    }
    
    /**
     * Get the floor block
     * @return Floor block
     */
    public Block getFloorBlock() {
        return floorBlock;
    }
    
    /**
     * Get the wall block
     * @return Wall block
     */
    public Block getWallBlock() {
        return wallBlock;
    }
    
    /**
     * Get the ceiling block
     * @return Ceiling block
     */
    public Block getCeilingBlock() {
        return ceilingBlock;
    }
    
    /**
     * Get the ambient light level
     * @return Ambient light level (0.0 - 1.0)
     */
    public float getAmbientLight() {
        return ambientLight;
    }
    
    /**
     * Check if this biome has water
     * @return Whether this biome has water
     */
    public boolean hasWater() {
        return hasWater;
    }
    
    /**
     * Check if this biome has lava
     * @return Whether this biome has lava
     */
    public boolean hasLava() {
        return hasLava;
    }
    
    /**
     * Check if this biome has vegetation
     * @return Whether this biome has vegetation
     */
    public boolean hasVegetation() {
        return hasVegetation;
    }
    
    /**
     * Get the minimum generation height
     * @return Minimum generation height
     */
    public int getMinY() {
        return minY;
    }
    
    /**
     * Get the maximum generation height
     * @return Maximum generation height
     */
    public int getMaxY() {
        return maxY;
    }
    
    /**
     * Called when generating this biome
     * @param random Random number generator
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public void onGenerate(Random random, int x, int y, int z) {
        // Default implementation is empty, can be overridden by subclasses
    }
    
    /**
     * Builder class for creating cave biomes
     */
    public static class Builder {
        private final ResourceLocation registryName;
        private final String name;
        private final int weight;
        private Block floorBlock = Blocks.STONE;
        private Block wallBlock = Blocks.STONE;
        private Block ceilingBlock = Blocks.STONE;
        private float ambientLight = 0.1f;
        private boolean hasWater = false;
        private boolean hasLava = false;
        private boolean hasVegetation = false;
        private int minY = 1;
        private int maxY = 128;
        
        /**
         * Create a new builder
         * @param registryName Unique registry name
         * @param name Display name
         * @param weight Spawn weight
         */
        public Builder(ResourceLocation registryName, String name, int weight) {
            this.registryName = registryName;
            this.name = name;
            this.weight = weight;
        }
        
        /**
         * Set the floor block
         * @param block Floor block
         * @return This builder
         */
        public Builder setFloorBlock(Block block) {
            this.floorBlock = block;
            return this;
        }
        
        /**
         * Set the wall block
         * @param block Wall block
         * @return This builder
         */
        public Builder setWallBlock(Block block) {
            this.wallBlock = block;
            return this;
        }
        
        /**
         * Set the ceiling block
         * @param block Ceiling block
         * @return This builder
         */
        public Builder setCeilingBlock(Block block) {
            this.ceilingBlock = block;
            return this;
        }
        
        /**
         * Set the ambient light level
         * @param light Ambient light level (0.0 - 1.0)
         * @return This builder
         */
        public Builder setAmbientLight(float light) {
            this.ambientLight = Math.max(0.0f, Math.min(1.0f, light));
            return this;
        }
        
        /**
         * Set whether this biome has water
         * @param hasWater Whether this biome has water
         * @return This builder
         */
        public Builder setHasWater(boolean hasWater) {
            this.hasWater = hasWater;
            return this;
        }
        
        /**
         * Set whether this biome has lava
         * @param hasLava Whether this biome has lava
         * @return This builder
         */
        public Builder setHasLava(boolean hasLava) {
            this.hasLava = hasLava;
            return this;
        }
        
        /**
         * Set whether this biome has vegetation
         * @param hasVegetation Whether this biome has vegetation
         * @return This builder
         */
        public Builder setHasVegetation(boolean hasVegetation) {
            this.hasVegetation = hasVegetation;
            return this;
        }
        
        /**
         * Set the minimum generation height
         * @param minY Minimum generation height
         * @return This builder
         */
        public Builder setMinY(int minY) {
            this.minY = Math.max(1, minY);
            return this;
        }
        
        /**
         * Set the maximum generation height
         * @param maxY Maximum generation height
         * @return This builder
         */
        public Builder setMaxY(int maxY) {
            this.maxY = Math.min(256, maxY);
            return this;
        }
        
        /**
         * Build the cave biome
         * @return The constructed cave biome
         */
        public CaveBiome build() {
            return new CustomCaveBiome(this);
        }
        
        /**
         * Custom cave biome implementation
         */
        private static class CustomCaveBiome extends CaveBiome {
            private final Block floorBlock;
            private final Block wallBlock;
            private final Block ceilingBlock;
            private final float ambientLight;
            private final boolean hasWater;
            private final boolean hasLava;
            private final boolean hasVegetation;
            private final int minY;
            private final int maxY;
            
            private CustomCaveBiome(Builder builder) {
                super(builder.registryName, builder.name, builder.weight);
                this.floorBlock = builder.floorBlock;
                this.wallBlock = builder.wallBlock;
                this.ceilingBlock = builder.ceilingBlock;
                this.ambientLight = builder.ambientLight;
                this.hasWater = builder.hasWater;
                this.hasLava = builder.hasLava;
                this.hasVegetation = builder.hasVegetation;
                this.minY = builder.minY;
                this.maxY = builder.maxY;
            }
            
            @Override
            public Block getFloorBlock() {
                return floorBlock;
            }
            
            @Override
            public Block getWallBlock() {
                return wallBlock;
            }
            
            @Override
            public Block getCeilingBlock() {
                return ceilingBlock;
            }
            
            @Override
            public float getAmbientLight() {
                return ambientLight;
            }
            
            @Override
            public boolean hasWater() {
                return hasWater;
            }
            
            @Override
            public boolean hasLava() {
                return hasLava;
            }
            
            @Override
            public boolean hasVegetation() {
                return hasVegetation;
            }
            
            @Override
            public int getMinY() {
                return minY;
            }
            
            @Override
            public int getMaxY() {
                return maxY;
            }
        }
    }
}