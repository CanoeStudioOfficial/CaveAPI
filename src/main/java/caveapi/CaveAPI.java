package caveapi;


import caveapi.caveapi.Tags;
import caveapi.utils.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class CaveAPI {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @SidedProxy(clientSide = "caveapi.utils.proxy.ClientProxy", serverSide = "caveapi.utils.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) { proxy.init(event); }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) { proxy.postInit(event); }

}