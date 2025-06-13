package com.canoestudio.caveapi.core;

import com.canoestudio.caveapi.world.gen.AquiferHandler;
import com.canoestudio.caveapi.world.gen.ICaveGenerator;
import com.canoestudio.caveapi.world.gen.LargeCaveGenerator;
import com.canoestudio.caveapi.world.gen.NoiseCaveGenerator;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class CaveRegistry {
    private static final List<ICaveGenerator> generators = new ArrayList<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        // 注册默认生成器
        registerGenerator(new NoiseCaveGenerator(0));
        registerGenerator(new LargeCaveGenerator());

        // 初始化含水层
        AquiferHandler.initialize(0);
    }

    public static void registerGenerator(ICaveGenerator generator) {
        generators.add(generator);
    }

    public static List<ICaveGenerator> getGenerators() {
        return generators;
    }

    public static boolean isCompatModLoaded(String modid) {
        return Loader.isModLoaded(modid);
    }
}