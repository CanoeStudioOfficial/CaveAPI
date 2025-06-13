package com.canoestudio.caveapi.core;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.canoestudio.caveapi.world.gen.AquiferHandler;
import com.canoestudio.caveapi.world.gen.ICaveGenerator;
import com.canoestudio.caveapi.world.gen.LargeCaveGenerator;
import com.canoestudio.caveapi.world.gen.NoiseCaveGenerator;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class CaveRegistry {
    private static final List<ICaveGenerator> generators = new ArrayList<>();
    private static boolean initialized = false;
    private static long worldSeed = 0;

    public static void init() {
        if (initialized) return;
        initialized = true;

        // 获取世界种子（如果没有则使用默认值）
        worldSeed = getWorldSeed();

        // 注册默认生成器
        refreshConfig();
    }

    private static long getWorldSeed() {
        try {
            // 尝试从服务器获取种子
            if (net.minecraft.server.MinecraftServer.getServer() != null) {
                return net.minecraft.server.MinecraftServer.getServer().getEntityWorld().getSeed();
            }
        } catch (Exception e) {
            // 客户端环境或其他异常
        }
        return System.currentTimeMillis(); // 默认种子
    }

    public static void registerGenerator(ICaveGenerator generator) {
        generators.add(generator);
    }

    public static List<ICaveGenerator> getGenerators() {
        return Collections.unmodifiableList(generators);
    }

    public static void refreshConfig() {
        generators.clear();

        // 使用新配置重新初始化生成器
        generators.add(new NoiseCaveGenerator(worldSeed));
        generators.add(new LargeCaveGenerator(worldSeed));

        // 含水层处理器也需要刷新
        AquiferHandler.refresh(worldSeed);
    }

    public static boolean isCompatModLoaded(String modid) {
        return Loader.isModLoaded(modid);
    }
}