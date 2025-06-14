package caveapi.core.registries;


import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CaveFeatures {
    public static final WorldGenerator ORE_GENERATOR = new CaveOreGenerator();

    public static void register() {
        // 1.12.2 使用 GameRegistry 注册世界生成器
        GameRegistry.registerWorldGenerator(ORE_GENERATOR, 0);
    }
}