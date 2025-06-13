
package caveapi.world.gen;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class CheeseCaveGenerator {

    public static void generate(World world, Biome biome, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        Random rand = new Random(seed ^ chunkX ^ chunkZ);

        // 每个区块尝试生成1-3个奶酪洞穴
        int caveCount = 1 + rand.nextInt(3);
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int i = 0; i < caveCount; i++) {
            // 随机洞穴中心位置
            int centerX = baseX + rand.nextInt(16);
            int centerZ = baseZ + rand.nextInt(16);
            int centerY = 5 + rand.nextInt(world.getHeight() - 10);

            // 随机洞穴大小
            float radiusH = 4 + rand.nextFloat() * 8;
            float radiusV = 3 + rand.nextFloat() * 6;

            // 生成球形洞穴
            generateSphericalCave(world, centerX, centerY, centerZ, radiusH, radiusV, rand);
        }
    }

    private static void generateSphericalCave(World world, int cx, int cy, int cz, float rh, float rv, Random rand) {
        int minX = (int) (cx - rh - 2);
        int maxX = (int) (cx + rh + 2);
        int minY = (int) (cy - rv - 2);
        int maxY = (int) (cy + rv + 2);
        int minZ = (int) (cz - rh - 2);
        int maxZ = (int) (cz + rh + 2);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    // 计算距离（椭圆体方程）
                    double dx = (x - cx) / rh;
                    double dy = (y - cy) / rv;
                    double dz = (z - cz) / rh;
                    double dist = dx*dx + dy*dy + dz*dz;

                    // 在边界内生成洞穴
                    if (dist <= 1.0) {
                        BlockPos pos = new BlockPos(x, y, z);
                        IBlockState state = world.getBlockState(pos);

                        if (isSolid(state)) {
                            world.setBlockToAir(pos);

                            // 洞穴底部生成水池
                            if (dist > 0.8 && y < cy && rand.nextFloat() < 0.3f) {
                                world.setBlockState(pos, Blocks.WATER.getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isSolid(IBlockState state) {
        return state.getMaterial().isSolid();
    }
}