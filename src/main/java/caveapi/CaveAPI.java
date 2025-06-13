package caveapi;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import caveapi.world.gen.CaveGenerator;
import caveapi.world.gen.NoiseCaveGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class CaveAPI {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    private static final Map<String, BiConsumer<World, Biome>> caveGenerators = new HashMap<>();

    // 默认生成器配置
    private static int noiseOctaves = 3;
    private static float noiseScale = 0.1F;

    public static void registerCaveGenerator(String id, BiConsumer<World, Biome> generator) {
        caveGenerators.put(id, generator);
    }

    public static void generateCaves(World world, int chunkX, int chunkZ) {
        // 触发预生成事件
        CaveGenerationEvent.Pre eventPre = new CaveGenerationEvent.Pre(world, chunkX, chunkZ);
        if (MinecraftForge.EVENT_BUS.post(eventPre))
            return;

        // 执行注册的生成器
        Biome biome = world.getBiome(new BlockPos(chunkX << 4, 0, chunkZ << 4));
        caveGenerators.values().forEach(gen -> gen.accept(world, biome));

        // 基础噪声洞穴
        NoiseCaveGenerator.generate(world, chunkX, chunkZ, noiseOctaves, noiseScale);

        // 触发后生成事件
        MinecraftForge.EVENT_BUS.post(new CaveGenerationEvent.Post(world, chunkX, chunkZ));
    }

    // 配置方法
    public static void configureNoise(int octaves, float scale) {
        noiseOctaves = octaves;
        noiseScale = scale;
    }



    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);

    }

}
