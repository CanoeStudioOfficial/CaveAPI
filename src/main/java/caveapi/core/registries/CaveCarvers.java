package caveapi.core.registries;

import caveapi.common.world.feature.carver.NoiseCarver;
import net.minecraft.world.gen.structure.MapGenStructureIO;

public class CaveCarvers {
    public static final NoiseCarver NOISE_CARVER = new NoiseCarver(1.0F);

    public static void register() {
        // 1.12.2 需注册到原版系统（由 ASM 实现注入）
    }
}