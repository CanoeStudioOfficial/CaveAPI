package org.canoestudio.caveapi.caveregister;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.canoestudio.caveapi.api.CaveSpawnControl;
import org.canoestudio.caveapi.api.ICaveEntity;

/**
 * Global handler for cave-related spawning logic.
 */
@Mod.EventBusSubscriber
public class CaveSpawnHandler {

    /**
     * Listen for entity spawn checks.
     * 1. If an entity implements ICaveEntity, it must pass the cave location check.
     * 2. If CaveSpawnControl is enabled, restricts underground mob spawns when players are on surface.
     */
    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        EntityLivingBase entity = event.getEntityLiving();
        BlockPos pos = event.getEntity().getPosition();
        World world = event.getWorld();
        
        // 1. ICaveEntity Check (Existing Logic)
        if (entity instanceof ICaveEntity) {
            ICaveEntity caveEntity = (ICaveEntity) entity;
            if (!caveEntity.isValidCaveLocation(world, pos)) {
                event.setResult(Event.Result.DENY);
                return;
            }
        }

        // 2. Surface Player Protection Logic (New Feature)
        if (CaveSpawnControl.isRestrictUndergroundSpawns() && entity instanceof IMob) {
            // Check if spawn is underground (below threshold and no sky access)
            if (pos.getY() < CaveSpawnControl.getSurfaceHeightThreshold() && !world.canSeeSky(pos)) {
                // Check for nearby surface player
                // Using getClosestPlayer to find if the spawn is triggered/near a surface player
                EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), CaveSpawnControl.getCheckRadius(), false);
                
                if (player != null) {
                    BlockPos playerPos = player.getPosition();
                    // If player is on surface (above threshold and can see sky)
                    if (playerPos.getY() >= CaveSpawnControl.getSurfaceHeightThreshold() && world.canSeeSky(playerPos)) {
                        event.setResult(Event.Result.DENY);
                    }
                }
            }
        }
    }
}
