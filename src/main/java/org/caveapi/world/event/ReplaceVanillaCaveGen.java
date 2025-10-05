package org.caveapi.world.event;

import org.caveapi.config.ConfigFile;
import org.caveapi.world.generator.NoGeneration;
import org.caveapi.world.generator.EarlyCaveHook;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReplaceVanillaCaveGen {

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onInitMapGen(InitMapGenEvent event) {
        final boolean isOriginalGen = event.getNewGen().equals(event.getOriginalGen());
        switch (event.getType()) {
            case CAVE :
                if (isOriginalGen) {
                    event.setNewGen(new EarlyCaveHook(null));
                } else {
                    event.setNewGen(new EarlyCaveHook(event.getNewGen()));
                }
                break;
            case RAVINE :
                if (isOriginalGen) {
                    event.setNewGen(NoGeneration.getInstance());
                }
                break;
            case NETHER_CAVE :
                if (ConfigFile.netherGenerate) {
                    if (isOriginalGen) {
                        event.setNewGen(new EarlyCaveHook(null));
                    } else {
                        event.setNewGen(new EarlyCaveHook(event.getNewGen()));
                    }
                }
                break;
            case MINESHAFT:
                if (!ConfigFile.enableMineshafts) {
                    if (isOriginalGen) {
                        event.setNewGen(NoGeneration.getInstance());
                    }
                }
        }
    }
}