package org.canoestudio.caveapi.caveregister;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.canoestudio.caveapi.api.ICaveEntity;

/**
 * Global handler for cave-related spawning logic.
 */
@Mod.EventBusSubscriber
public class CaveSpawnHandler {

    /**
     * Listen for entity spawn checks.
     * If an entity implements ICaveEntity, it must pass the cave location check.
     */
    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        EntityLivingBase entity = event.getEntityLiving();
        
        if (entity instanceof ICaveEntity) {
            ICaveEntity caveEntity = (ICaveEntity) entity;
            if (!caveEntity.isValidCaveLocation(event.getWorld(), event.getEntity().getPosition())) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
