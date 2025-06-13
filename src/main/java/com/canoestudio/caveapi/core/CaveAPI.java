package com.canoestudio.caveapi.core;

import com.canoestudio.caveapi.event.CaveGenerationEvent;
import com.canoestudio.caveapi.world.gen.AquiferHandler;
import com.canoestudio.caveapi.world.gen.CaveGeneratorRegistry;
import com.canoestudio.caveapi.world.gen.ICaveGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.MinecraftForge;
import java.util.List;

public class CaveAPI {
    /**
     * 供其他模组注册自定义洞穴生成器
     * @param generator 实现ICaveGenerator接口的生成器
     */
    public static void registerCaveGenerator(ICaveGenerator generator) {
        CaveGeneratorRegistry.registerGenerator(generator);
    }

    /**
     * 生成洞穴的核心方法
     * @param world 世界实例
     * @param primer 区块原始数据
     * @param chunkX 区块X坐标
     * @param chunkZ 区块Z坐标
     */
    public static void generateCaves(World world, ChunkPrimer primer, int chunkX, int chunkZ) {
        CaveGenerationEvent.Pre preEvent = new CaveGenerationEvent.Pre(world, primer, chunkX, chunkZ);
        MinecraftForge.EVENT_BUS.post(preEvent);

        if (!preEvent.isCanceled()) {
            List<ICaveGenerator> generators = CaveGeneratorRegistry.getGenerators();

            // 执行所有注册的生成器
            for (ICaveGenerator gen : generators) {
                gen.generate(world, primer, chunkX, chunkZ);
            }

            // 处理含水层
            AquiferHandler.handleAquifers(primer, chunkX, chunkZ);

            // 后期事件
            CaveGenerationEvent.Post postEvent = new CaveGenerationEvent.Post(world, primer, chunkX, chunkZ);
            MinecraftForge.EVENT_BUS.post(postEvent);
        }
    }
}