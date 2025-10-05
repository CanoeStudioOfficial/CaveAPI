package org.caveapi.world.event;

import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.caveapi.config.ConfigFile;

public class DisablePopulateChunkEvent {

    @SubscribeEvent
    public static void onPopulateChunk(PopulateChunkEvent.Populate event) {
        if (!ConfigFile.enableWaterLakes && event.getType() == EventType.LAKE) {
            event.setResult(Event.Result.DENY);
        } else if (!ConfigFile.enableLavaLakes && event.getType() == EventType.LAVA) {
            event.setResult(Event.Result.DENY);
        }
    }
}
