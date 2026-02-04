package org.canoestudio.caveapi.api;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import java.util.Set;

/**
 * Registry for managing associations between ores and stone types.
 */
public interface IOreStoneRegistry {
    /**
     * Register a base stone type that can host ores.
     * @param stone The stone block
     * @param textureLocation The resource location of its primary texture
     */
    void registerBaseStone(Block stone, ResourceLocation textureLocation);

    /**
     * Register an ore type and its overlay texture.
     * @param ore The ore block (default/original)
     * @param overlayLocation The resource location of the ore's transparent overlay texture
     */
    void registerOre(Block ore, ResourceLocation overlayLocation);

    /**
     * Link an ore to a specific stone type.
     * @param ore The original ore
     * @param stone The base stone
     * @return The combined block (dynamically created or mapped)
     */
    Block getOreInStone(Block ore, Block stone);

    Set<Block> getRegisteredStones();
    Set<Block> getRegisteredOres();
}
