package caveapi;

import caveapi.caveapi.Tags;
import caveapi.world.CaveGenerationEvent;
import caveapi.world.gen.AquiferGenerator;
import caveapi.world.gen.CheeseCaveGenerator;
import caveapi.world.gen.NoodleCaveGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        LOGGER.info("Initializing {} - Adding 1.17+ Cave Generators", Tags.MOD_NAME);

        // 注册新的洞穴生成器（修复lambda参数）
        registerCaveGenerator("noise", (world, biome) ->
                NoiseCaveGenerator.generate(world, biome, noiseOctaves, (int) noiseScale));

        registerCaveGenerator("cheese", (world, biome) ->
                CheeseCaveGenerator.generate(world, biome, 0, 0)); // 固定区块坐标

        registerCaveGenerator("noodle", (world, biome) ->
                NoodleCaveGenerator.generate(world, biome, 0, 0)); // 固定区块坐标

        // 注册含水层生成器事件监听器
        MinecraftForge.EVENT_BUS.register(new AquiferGenerator());
    }
}