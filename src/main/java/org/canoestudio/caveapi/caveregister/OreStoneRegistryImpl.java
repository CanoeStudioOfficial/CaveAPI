package org.canoestudio.caveapi.caveregister;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import org.canoestudio.caveapi.api.IOreStoneRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the Ore-Stone association system.
 */
public class OreStoneRegistryImpl implements IOreStoneRegistry {
    private static final OreStoneRegistryImpl INSTANCE = new OreStoneRegistryImpl();
    
    private final Map<Block, ResourceLocation> stoneTextures = new HashMap<>();
    private final Map<Block, ResourceLocation> oreOverlays = new HashMap<>();
    private final Map<String, Block> linkedOreCache = new HashMap<>();

    public static OreStoneRegistryImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void registerBaseStone(Block stone, ResourceLocation textureLocation) {
        stoneTextures.put(stone, textureLocation);
    }

    @Override
    public void registerOre(Block ore, ResourceLocation overlayLocation) {
        oreOverlays.put(ore, overlayLocation);
    }

    @Override
    public Block getOreInStone(Block ore, Block stone) {
        String key = ore.getRegistryName() + "@" + stone.getRegistryName();
        // In a real implementation, this would return a dynamic block or a pre-registered variant
        return linkedOreCache.getOrDefault(key, ore);
    }

    @Override
    public Set<Block> getRegisteredStones() {
        return new HashSet<>(stoneTextures.keySet());
    }

    @Override
    public Set<Block> getRegisteredOres() {
        return new HashSet<>(oreOverlays.keySet());
    }

    public ResourceLocation getStoneTexture(Block stone) {
        return stoneTextures.get(stone);
    }

    public ResourceLocation getOreOverlay(Block ore) {
        return oreOverlays.get(ore);
    }
}
