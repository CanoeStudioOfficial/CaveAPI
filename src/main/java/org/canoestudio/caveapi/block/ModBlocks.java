package org.canoestudio.caveapi.block;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Mod Blocks Registry
 */
@Mod.EventBusSubscriber
public class ModBlocks {
    
    @GameRegistry.ObjectHolder("caveapi:cave_air")
    public static final Block CAVE_AIR = null;

    /**
     * Register blocks
     * @param event Registry event
     */
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new BlockCaveAir());
    }
}
