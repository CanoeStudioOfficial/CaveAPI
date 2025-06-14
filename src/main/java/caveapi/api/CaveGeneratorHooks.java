package caveapi.api;

import caveapi.core.CaveConfig;

public class CaveGeneratorHooks {
    public static void generateCaves(ChunkProviderOverworld provider, int x, int z, ChunkPrimer primer) {
        if (CaveConfig.generateNoiseCarvers) {
            // 实现噪声洞穴生成逻辑
            new NoiseCaveGenerator().generate(provider.world, x, z, primer);
        }
    }
}