package caveapi.api;

import caveapi.CaveAPI;
import caveapi.world.gen.CaveType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

import java.util.function.BiConsumer;

/**
 * 洞穴生成系统API接口
 * 允许其他模组注册自定义洞穴生成器和事件处理器
 */
public class CaveAPIRegistry {

    /**
     * 注册自定义洞穴生成器
     * @param type 洞穴类型标识（建议使用CaveType枚举或自定义字符串）
     * @param generator 生成器逻辑 (world, biome) -> {...}
     */
    public static void registerCaveGenerator(String type, BiConsumer<World, Biome> generator) {
        CaveAPI.registerCaveGenerator(type, generator);
    }

    /**
     * 注销指定类型的洞穴生成器
     * @param type 要注销的生成器类型
     */
    public static void unregisterCaveGenerator(String type) {
        CaveAPI.unregisterCaveGenerator(type);
    }

    /**
     * 注册含水层事件监听器（简化版）
     * @param aquiferGenerator 含水层生成器实例
     */
    public static void registerAquiferGenerator(Object aquiferGenerator) {
        MinecraftForge.EVENT_BUS.register(aquiferGenerator);
    }

    /**
     * 注册自定义洞穴事件监听器
     * @param eventHandler 事件处理器实例
     * @param priority 事件优先级（默认为NORMAL）
     */
    public static void registerCaveEventListener(Object eventHandler, EventPriority priority) {
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }

    /**
     * 获取默认含水层生成器实例
     * @return 含水层生成器实例（可直接注册）
     */
    public static Object getDefaultAquiferGenerator() {
        return new caveapi.world.gen.AquiferGenerator();
    }
}