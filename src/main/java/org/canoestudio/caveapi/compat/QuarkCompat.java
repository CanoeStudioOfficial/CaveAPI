package org.canoestudio.caveapi.compat;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.canoestudio.caveapi.api.CaveBiome;

/**
 * Compatibility layer for Quark mod.
 */
public class QuarkCompat implements IModCompat {
    @Override
    public void setup() {
        UniversalCompatHandler.registerRemapper((world, pos, currentState, type) -> {
            // Example logic: if we are in a specific condition, replace with Quark blocks
            // This is now handled through the universal system
            return currentState;
        });
    }
    
    /**
     * Utility to check if a block is from Quark's cave system
     */
    public static boolean isQuarkCaveBlock(Block block) {
        ResourceLocation registryName = block.getRegistryName();
        return registryName != null && registryName.toString().startsWith("quark:");
    }
}
