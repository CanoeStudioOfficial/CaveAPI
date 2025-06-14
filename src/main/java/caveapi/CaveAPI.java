package caveapi;

import caveapi.caveapi.Tags;


import caveapi.core.registries.CaveBiomes;
import caveapi.core.registries.CaveCarvers;
import caveapi.core.registries.CaveFeatures;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class CaveAPI {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        CaveBiomes.register();
        CaveFeatures.register();
        CaveCarvers.register();
    }


}