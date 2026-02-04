package org.canoestudio.caveapi.api;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

/**
 * Base class for cave-dwelling mobs.
 * Automatically handles spawning logic to ensure they only appear in caves.
 */
public abstract class EntityCaveMob extends EntityMob implements ICaveEntity {
    
    public EntityCaveMob(World worldIn) {
        super(worldIn);
    }

    /**
     * Checks if the entity's current position is a valid spawn location.
     * Overrides standard mob logic to include cave-specific checks.
     */
    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && isValidCaveLocation(this.world, this.getPosition());
    }
}
