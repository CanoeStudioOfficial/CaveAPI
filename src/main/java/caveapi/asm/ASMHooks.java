package caveapi.asm;


import caveapi.common.world.feature.carver.NoiseCarver;
import caveapi.core.CaveConfig;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.World;

public class ASMHooks {
    public static void afterChunkPrimed(ChunkPrimer primer, int chunkX, int chunkZ) {
        // 检查是否启用洞穴生成
        if (!CaveConfig.enableNoiseCarver) return;

        // 获取世界实例
        World world = primer.worldObj;

        // 检查维度是否是主世界
        if (world.provider.getDimension() != 0) return;

        // 初始化洞穴生成器
        NoiseCarver carver = new NoiseCarver();

        try {
            // 在区块上生成洞穴
            carver.generate(world, chunkX, chunkZ, primer);
        } catch (Exception e) {
            // 安全捕获异常，防止崩溃
            System.err.println("洞穴生成器错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}