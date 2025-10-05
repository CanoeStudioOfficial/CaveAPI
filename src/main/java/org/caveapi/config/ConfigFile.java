package org.caveapi.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.caveapi.CaveAPI;

@Mod.EventBusSubscriber
@Config(modid = CaveAPI.MODID)
public class ConfigFile {

    @Comment({
        "Whether vanilla stone clusters--including andesite,",
        "diorite, and granite--should spawn in the world."})
    @RequiresMcRestart
    public static boolean enableVanillaStoneClusters = false;

    @Comment("Whether to enable vanilla water lakes underground.")
    @RequiresMcRestart
    public static boolean enableWaterLakes = false;

    @Comment("Whether to enable vanilla lava lakes underground.")
    @RequiresMcRestart
    public static boolean enableLavaLakes = false;

    @Comment("Whether to enable vanilla mineshafts underground.")
    @RequiresMcRestart
    public static boolean enableMineshafts = true;

    @Comment({
        "Whether this mod will attempt to run simultaneously",
        "with one other cave generation mod, such as Worley's",
        "Caves or Yung's Better Caves."})
    @RequiresMcRestart
    public static boolean otherGeneratorEnabled = false;

    @Comment({
        "When this field is set to true, PresetTester is allowed to",
        "crash the game when more serious errors are detected. Users",
        "who are more serious about creating cleaner and more efficient",
        "presets should consider enabling this field to make sure that",
        "nothing slips by."})
    public static boolean strictPresets = false;

    @Comment({
        "When this field is set to true, PresetReader will skip over",
        "any invalid presets and simply not load them. Make sure to",
        "check your log to determine if any preset erred."
    })
    public static boolean ignoreInvalidPresets = false;

    @Comment({
        "A list of dimensions where HeightMapLocator will check for the",
        "surface to avoid spawning caverns in water. Disable this in your",
        "dimension if you don't have regular oceans spawning."
    })
    @RequiresWorldRestart
    public static int[] heightMapDims = { 0 };

    @Comment("The chunk search range for tunnel and ravine features.")
    public static int mapRange = 8;

    @Comment({
        "The range in chunks to read biomes for features that use",
        "distance-based biome testing."})
    public static int biomeRange = 2;

    @Comment("Whether to override and replace caverns in the nether.")
    @RequiresMcRestart
    public static boolean netherGenerate = false;

    @Comment({
        "Whether to automatically format your preset files. They will",
        "still be reformatted if values are updated."})
    public static boolean autoFormat = true;

    @Comment({
        "Whether to automatically generate preset files inside of",
        "cavegenerator/generated. This will help you see how your",
        "variables are getting expanded every time you reload your",
        "presets."})
    public static boolean autoGenerate = true;

    @Comment({
        "Whether to automatically update import files, as much",
        "as possible. Note that compatibility updates will still",
        "occur."})
    public static boolean updateImports = true;

    @Comment({
            "A list of proxy dimensions to get a biome provider from in place of the dimension's normal biome provider.",
            "This list must exactly correspond with archaeneUseProxyDims in length."
    })
    @RequiresWorldRestart
    public static int[] archaeneProxyDims = { 0 };

    @Comment({
            "A list of what dimension uses a given proxy dimension in archaeneProxyDims.",
            "This list must exactly correspond with archaneProxyDims in length."
    })
    @RequiresWorldRestart
    public static int[] archaeneUseProxyDims = { 0 };

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(CaveAPI.MODID)) {
            ConfigManager.sync(CaveAPI.MODID, Config.Type.INSTANCE);
        }
    }
}
