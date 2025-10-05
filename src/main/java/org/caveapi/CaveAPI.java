package org.caveapi;

import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.caveapi.caveapi.Tags;
import org.caveapi.commands.CommandCave;
import org.caveapi.config.CavePreset;
import org.caveapi.io.JarFiles;
import org.caveapi.noise.CachedNoiseHelper;
import org.caveapi.world.GeneratorController;
import org.caveapi.world.data.WorldDataHandler;
import org.caveapi.world.event.DisablePopulateChunkEvent;
import org.caveapi.world.event.DisableVanillaStoneGen;
import org.caveapi.world.event.ReplaceVanillaCaveGen;
import org.caveapi.world.feature.FeatureCaveHook;
import org.caveapi.world.feature.StructureSpawner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION,
    dependencies = "after:worleycaves;",
    acceptableRemoteVersions = "*"
)
@Log4j2
public class CaveAPI {

    /** The main instance of this mod, as required by Forge. */
    @Instance public static CaveAPI instance;

    /** This mod's ID and namespace. */
    public static final String MODID = Tags.MOD_ID;

    /** A non-null map of ID -> CaveGenerator to be filled on WorldEvent.Load. */
    public final Map<String, GeneratorController> generators = new TreeMap<>();

    /** A non-null map of ID -> GeneratorSettings to be filled at runtime. */
    public final Map<String, CavePreset> presets = new TreeMap<>();

    /** A non-null map of ID -> Structure to be filled at runtime. */
    public final Map<String, Template> structures = new HashMap<>();


    @Mod.EventHandler
    public static void onPreInit(FMLPreInitializationEvent event) {
        WorldDataHandler.register();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public static void init(FMLInitializationEvent event) {
        JarFiles.copyFiles();
        StructureSpawner.loadAllStructures(instance.structures);
        CaveInit.initPresets(instance.presets);
        MinecraftForge.EVENT_BUS.register(CaveInit.class);
        MinecraftForge.TERRAIN_GEN_BUS.register(ReplaceVanillaCaveGen.class);
        MinecraftForge.ORE_GEN_BUS.register(DisableVanillaStoneGen.class);
        MinecraftForge.TERRAIN_GEN_BUS.register(DisablePopulateChunkEvent.class);
        GameRegistry.registerWorldGenerator(new FeatureCaveHook(), 0);
        log.info("Cave Generator init phase complete.");
    }

    @EventHandler
    @SuppressWarnings("unused")
    public static void onServerStartingEvent(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCave());
        log.info("Cave Generator commands registered.");
    }

    @EventHandler
    @SuppressWarnings("unused")
    public static void onServerStoppingEvent(FMLServerStoppingEvent event) {
        log.info("Unloading generators.");
        CaveAPI.instance.generators.clear();
        CachedNoiseHelper.removeAll();
    }

    /** Loads a generator for the current dimension, if applicable. */
    public Map<String, GeneratorController> loadGenerators(World world) {
        if (presets.isEmpty()) {
            return generators; // i.e. never load them.
        }
        if (generators.isEmpty()) {
            for (Map.Entry<String, CavePreset> entry : presets.entrySet()) {
                final CavePreset preset = entry.getValue();
                if (preset.enabled) {
                    world.rand.setSeed(world.getSeed());
                    generators.put(entry.getKey(), GeneratorController.from(preset, world));
                }
            }
        }
        //return generators;
        return Collections.singletonMap("a", GeneratorController.from(generators));
    }
}