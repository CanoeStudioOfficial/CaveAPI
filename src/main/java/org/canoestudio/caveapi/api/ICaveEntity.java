package org.canoestudio.caveapi.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for entities that only spawn in cave environments.
 * Provides a standard way for entities to check if they are in a valid cave location.
 */
public interface ICaveEntity {
    
    /**
     * Check if the current position is a valid cave environment for this entity.
     * Implementation should typically check for cave air or depth.
     * 
     * @param world The world
     * @param pos The position to check
     * @return True if the location is a valid cave environment
     */
    default boolean isValidCaveLocation(World world, BlockPos pos) {
        // Use the helper to check if the position is marked as Cave Air
        // and ensure we are deep enough (typically below sea level)
        return CaveIdentifyHelper.isCave(world, pos) || (pos.getY() < world.getSeaLevel() - 10 && !world.canSeeSky(pos));
    }
}
