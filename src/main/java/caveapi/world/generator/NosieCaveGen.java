package caveapi.world.generator;

import caveapi.noise.OpenSimplexNoise;
import caveapi.utils.worldgen.CaveGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class NosieCaveGen implements CaveGenerator {

    private static final float CHEESE_NOISE_SCALE = 0.02f; // 低频率
    private static final float NOODLE_NOISE_SCALE = 0.08f; // 高频率
    private static final float CHEESE_NOISE_THRESHOLD = 0.1f;
    private static final float NOODLE_NOISE_THRESHOLD = 0.5f;
    private static final float FUSION_THRESHOLD = 0.0f;
    private static final int MIN_Y = 10;
    private static final int MAX_Y = 54;
    private static final int CHUNK_SIZE = 16;

    public void generate(final World world, final int chunkX, final int chunkZ, final ChunkPrimer primer) {
        this.generateCaves(world, chunkX, chunkZ, primer);
    }

    private void generateCaves(final World world, final int chunkX, final int chunkZ, final ChunkPrimer primer) {
        final Random rand = new Random(chunkX * 341873128712L + chunkZ * 132897987541L ^ world.getSeed());
        final long seed = world.getSeed();
        final int baseX = chunkX * 16;
        final int baseZ = chunkZ * 16;
        this.generateCaveSystem(primer, seed, baseX, baseZ);
    }

    private void generateCaveSystem(final ChunkPrimer primer, final long seed, final int baseX, final int baseZ) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 10; y < 54; ++y) {
                    // 芝士洞穴噪声 - 低频率、高振幅
                    final double cheeseNX = (baseX + x) * CHEESE_NOISE_SCALE;
                    final double cheeseNY = y * CHEESE_NOISE_SCALE;
                    final double cheeseNZ = (baseZ + z) * CHEESE_NOISE_SCALE;
                    final float cheeseNoise = OpenSimplexNoise.noise3_ImproveXY(seed, cheeseNX, cheeseNY, cheeseNZ);

                    // 面条洞穴噪声 - 高频率、低振幅
                    final double noodleNX = (baseX + x) * NOODLE_NOISE_SCALE;
                    final double noodleNY = y * NOODLE_NOISE_SCALE;
                    final double noodleNZ = (baseZ + z) * NOODLE_NOISE_SCALE;
                    final float noodleNoise = OpenSimplexNoise.noise3_ImproveXY(seed + 12345L, noodleNX, noodleNY, noodleNZ);

                    // 融合噪声 - 使用MAX操作
                    final float fusedNoise = Math.max(
                            cheeseNoise > CHEESE_NOISE_THRESHOLD ? cheeseNoise - CHEESE_NOISE_THRESHOLD : 0f,
                            noodleNoise > NOODLE_NOISE_THRESHOLD ? noodleNoise - NOODLE_NOISE_THRESHOLD : 0f
                    );

                    // 生成洞穴 - 当融合噪声大于阈值时生成空气
                    if (fusedNoise > FUSION_THRESHOLD) {
                        primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }
}
