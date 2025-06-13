// NoodleCaveGenerator.java
package caveapi.world.gen;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class NoodleCaveGenerator {

    public static void generate(World world, Biome biome, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        Random rand = new Random(seed ^ chunkX ^ chunkZ);
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        // 每个区块尝试生成2-4个面条洞穴
        int caveCount = 2 + rand.nextInt(3);
        for (int i = 0; i < caveCount; i++) {
            // 随机起点位置
            int startX = baseX + rand.nextInt(16);
            int startZ = baseZ + rand.nextInt(16);
            int startY = 5 + rand.nextInt(world.getHeight() - 10);

            // 随机洞穴参数
            int length = 30 + rand.nextInt(50);
            float radius = 1.0f + rand.nextFloat() * 1.5f;

            // 生成洞穴
            generateWormCave(world, startX, startY, startZ, length, radius, rand);
        }
    }

    private static void generateWormCave(World world, int startX, int startY, int startZ, int length, float radius, Random rand) {
        Vec3d direction = new Vec3d(
                rand.nextDouble() - 0.5,
                (rand.nextDouble() - 0.5) * 0.2,
                rand.nextDouble() - 0.5
        ).normalize();

        Vec3d position = new Vec3d(startX, startY, startZ);

        for (int i = 0; i < length; i++) {
            // 生成当前段
            generateCaveSegment(world, position, radius);

            // 移动位置
            position = position.add(direction.scale(2.0));

            // 随机改变方向
            if (rand.nextFloat() < 0.25f) {
                direction = direction.add(
                        (rand.nextDouble() - 0.5) * 0.4,
                        (rand.nextDouble() - 0.5) * 0.2,
                        (rand.nextDouble() - 0.5) * 0.4
                ).normalize();
            }

            // 稍微向上趋势
            if (rand.nextFloat() < 0.1f) {
                direction = direction.add(0, 0.1, 0).normalize();
            }

            // 偶尔生成小水池
            if (i % 10 == 0 && rand.nextFloat() < 0.3f) {
                generateSmallPool(world, position, radius * 1.5f, rand);
            }
        }
    }

    private static void generateCaveSegment(World world, Vec3d center, float radius) {
        int r = (int) Math.ceil(radius);
        int cx = (int) center.x;
        int cy = (int) center.y;
        int cz = (int) center.z;

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    double dist = Math.sqrt(dx*dx + dy*dy*4 + dz*dz) / radius;
                    if (dist <= 1.0) {
                        BlockPos pos = new BlockPos(cx + dx, cy + dy, cz + dz);
                        IBlockState state = world.getBlockState(pos);

                        if (isSolid(state)) {
                            world.setBlockToAir(pos);
                        }
                    }
                }
            }
        }
    }

    private static void generateSmallPool(World world, Vec3d center, float radius, Random rand) {
        int cx = (int) center.x;
        int cy = (int) center.y;
        int cz = (int) center.z;

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = -1; dy <= 0; dy++) {
                    BlockPos pos = new BlockPos(cx + dx, cy + dy, cz + dz);
                    IBlockState state = world.getBlockState(pos);

                    if (state.getBlock().isAir(state, world, pos) ||
                            state.getMaterial() == Material.WATER) {

                        world.setBlockState(pos, Blocks.WATER.getDefaultState());
                    }
                }
            }
        }
    }

    private static boolean isSolid(IBlockState state) {
        return state.getMaterial().isSolid();
    }
}