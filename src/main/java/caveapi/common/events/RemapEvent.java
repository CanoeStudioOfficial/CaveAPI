package caveapi.common.events;

import caveapi.caveapi.Tags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber
public class RemapEvent {
    @SubscribeEvent
    public static void updateBiomeID(RegistryEvent.MissingMappings<Biome> event) {
        for (RegistryEvent.MissingMappings.Mapping<Biome> mapping : event.getMappings()) {
            ResourceLocation oldId = mapping.key;
            String namespace = oldId.getResourceDomain();
            String path = oldId.getResourcePath();

            if (namespace.equals("bgcore") || namespace.equals("cavebiomes")) {
                Biome newBiome = ForgeRegistries.BIOMES.getValue(
                        new ResourceLocation(Tags.MOD_ID, path)
                );
                if (newBiome != null) {
                    mapping.remap(newBiome);
                }
            }
        }
    }
}