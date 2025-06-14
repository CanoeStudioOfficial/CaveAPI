package caveapi.core;

import caveapi.caveapi.Tags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Tags.MOD_ID)
public class CaveConfig {
    @Config.Comment("是否启用噪声洞穴生成器")
    @Config.Name("启用洞穴生成器")
    public static boolean enableNoiseCarver = true;

    @Config.Comment("洞穴生成概率 (0.0-1.0)")
    @Config.Name("洞穴概率")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    public static double caveProbability = 0.7;

    @Config.Comment("洞穴最大Y坐标 (0-255)")
    @Config.Name("最大高度")
    @Config.RangeInt(min = 1, max = 255)
    public static int maxCaveHeight = 128;

    @Config.Comment("洞穴最小Y坐标 (0-255)")
    @Config.Name("最小高度")
    @Config.RangeInt(min = 0, max = 254)
    public static int minCaveHeight = 5;

    @Config.Comment("洞穴复杂度 (1.0-5.0)")
    @Config.Name("复杂度")
    @Config.RangeDouble(min = 1.0, max = 5.0)
    public static double caveComplexity = 2.5;

    @Config.Comment("Whether to generate experimental small noise caves")
    @Config.Name("generate_small_noise_caves")
    public static boolean generateNoiseCarvers = false;

    @Mod.EventBusSubscriber(modid = Tags.MOD_ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Tags.MOD_ID)) {
                ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
