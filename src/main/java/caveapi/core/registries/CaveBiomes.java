package caveapi.core.registries;

import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CaveBiomes {
    public static final Biome CAVE = new CaveBiome().setRegistryName("cavebiomes:caves");

    public static void register() {
        // 1.12.2 使用 ForgeRegistries 注册生物群系
        ForgeRegistries.BIOMES.register(CAVE);

        // 添加到洞穴生成权重
        BiomeManager.addBiome(BiomeManager.BiomeType.ICY, new BiomeManager.BiomeEntry(CAVE, 10));
    }

    public static class CaveBiome extends Biome {
        public CaveBiome() {
            super(new BiomeProperties("Caves")
                    .setTemperature(0.8F)
                    .setRainfall(0.4F)
                    .setBaseHeight(0.125F)
                    .setHeightVariation(0.05F));

            // 手动设置效果和生成
            setWaterColor(4159204);
            setWaterFogColor(329011);
            spawnableCreatureList.clear();
            spawnableMonsterList.clear();
            // 添加原版洞穴生物
            spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 100, 1, 4));
            spawnableMonsterList.add(new SpawnListEntry(EntityCaveSpider.class, 100, 1, 4));
        }
    }
}