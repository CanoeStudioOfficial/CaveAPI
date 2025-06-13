package com.canoestudio.caveapi.world.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CaveGeneratorRegistry {
    private static final List<ICaveGenerator> generators = new ArrayList<>();
    private static final List<ICaveGenerator> immutableGenerators =
            Collections.unmodifiableList(generators);

    private static long worldSeed = 0;

    public static void registerGenerator(ICaveGenerator generator) {
        generators.add(generator);
    }

    public static List<ICaveGenerator> getGenerators() {
        return immutableGenerators;
    }

    public static void init(long seed) {
        worldSeed = seed;
        refresh();
    }

    public static void refresh() {
        // 保留第三方生成器
        List<ICaveGenerator> thirdPartyGenerators = new ArrayList<>();
        for (ICaveGenerator gen : generators) {
            if (!(gen instanceof NoiseCaveGenerator) && !(gen instanceof LargeCaveGenerator)) {
                thirdPartyGenerators.add(gen);
            }
        }

        generators.clear();
        generators.addAll(thirdPartyGenerators);

        // 重新添加默认生成器
        generators.add(new NoiseCaveGenerator(worldSeed));
        generators.add(new LargeCaveGenerator(worldSeed));
    }
}