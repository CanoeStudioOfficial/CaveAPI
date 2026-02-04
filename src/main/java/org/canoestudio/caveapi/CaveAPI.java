package org.canoestudio.caveapi;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.canoestudio.caveapi.caveregister.CaveBiomeRegistry;
import org.canoestudio.caveapi.compat.ModCompatManager;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION
)
@Mod.EventBusSubscriber
public class CaveAPI {
    private static CaveAPI instance;
    
    public CaveAPI() {
        instance = this;
    }
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Initialize cave biome registry
        CaveBiomeRegistry.createRegistry();
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Initialize cross-mod compatibility
        ModCompatManager.init();
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Post initialization logic
    }
    
    public static CaveAPI getInstance() {
        return instance;
    }
}