package caveapi.common.world.feature.carver;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

import java.util.Random;

public class NoiseCarver extends MapGenBase {
    private long seed;
    private NoiseGeneratorOctaves caveNoise;
    private NoiseGeneratorOctaves offsetNoise;
    private NoiseGeneratorOctaves scaleNoise;

    public NoiseCarver() {
        super(256); // 最大高度
    }

    @Override
    public void generate(World world, int chunkX, int chunkZ, Chunk chunk) {
        // 初始化噪声生成器
        if (this.caveNoise == null || this.seed != world.getSeed()) {
            this.seed = world.getSeed();
            Random seedRandom = new Random(this.seed);
            this.caveNoise = new NoiseGeneratorOctaves(seedRandom, 6);
            this.offsetNoise = new NoiseGeneratorOctaves(seedRandom, 3);
            this.scaleNoise = new NoiseGeneratorOctaves(seedRandom, 1);
        }

        // 获取高度图
        int[] heights = new int[256];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                heights[(x * 16) + z] = world.getTopSolidOrLiquidBlock(
                        new BlockPos((chunkX << 4) + x, 0, (chunkZ << 4) + z)
                ).getY();
            }
        }

        int chunkStartX = chunkX << 4;
        int chunkStartZ = chunkZ << 4;
        double[][][] noiseData = new double[2][5][9];

        // 初始化噪声列
        for(int noiseZ = 0; noiseZ < 5; ++noiseZ) {
            noiseData[0][noiseZ] = new double[9];
            sampleNoiseColumn(noiseData[0][noiseZ], chunkX * 4, chunkZ * 4 + noiseZ);
            noiseData[1][noiseZ] = new double[9];
        }

        for(int noiseX = 0; noiseX < 4; ++noiseX) {
            for (int noiseZ = 0; noiseZ < 5; ++noiseZ) {
                sampleNoiseColumn(noiseData[1][noiseZ], chunkX * 4 + noiseX + 1, chunkZ * 4 + noiseZ);
            }

            for (int noiseZ = 0; noiseZ < 4; ++noiseZ) {
                for (int noiseY = 7; noiseY >= 0; --noiseY) {
                    double x0z0y0 = noiseData[0][noiseZ][noiseY];
                    double x0z1y0 = noiseData[0][noiseZ + 1][noiseY];
                    double x1z0y0 = noiseData[1][noiseZ][noiseY];
                    double x1z1y0 = noiseData[1][noiseZ + 1][noiseY];
                    double x0z0y1 = noiseData[0][noiseZ][noiseY + 1];
                    double x0z1y1 = noiseData[0][noiseZ + 1][noiseY + 1];
                    double x1z0y1 = noiseData[1][noiseZ][noiseY + 1];
                    double x1z1y1 = noiseData[1][noiseZ + 1][noiseY + 1];

                    for (int pieceY = 7; pieceY >= 0; --pieceY) {
                        int realY = noiseY * 8 + pieceY;
                        double yLerp = (double) pieceY / 8.0;

                        double x0z0 = MathHelper.lerp(yLerp, x0z0y0, x0z0y1);
                        double x1z0 = MathHelper.lerp(yLerp, x1z0y0, x1z0y1);
                        double x0z1 = MathHelper.lerp(yLerp, x0z1y0, x0z1y1);
                        double x1z1 = MathHelper.lerp(yLerp, x1z1y0, x1z1y1);

                        for (int pieceX = 0; pieceX < 4; ++pieceX) {
                            int realX = chunkStartX + noiseX * 4 + pieceX;
                            int localX = realX & 15;
                            double xLerp = (double) pieceX / 4.0;
                            double z0 = MathHelper.lerp(xLerp, x0z0, x1z0);
                            double z1 = MathHelper.lerp(xLerp, x0z1, x1z1);

                            for (int pieceZ = 0; pieceZ < 4; ++pieceZ) {
                                int realZ = chunkStartZ + noiseZ * 4 + pieceZ;
                                int localZ = realZ & 15;
                                double zLerp = (double) pieceZ / 4.0;
                                double density = MathHelper.lerp(zLerp, z0, z1);

                                int heightAt = heights[localX * 16 + localZ];

                                if (realY > heightAt - 12) {
                                    density += 4.8;
                                }

                                if (realY > heightAt) {
                                    continue;
                                }

                                if (density < 0.0) {
                                    carveBlock(chunk, realX, realY, realZ, heightAt);
                                }
                            }
                        }
                    }
                }
            }

            double[][] xColumn = noiseData[0];
            noiseData[0] = noiseData[1];
            noiseData[1] = xColumn;
        }
    }

    private void carveBlock(Chunk chunk, int x, int y, int z, int heightAt) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState current = chunk.getBlockState(pos);

        if (current.getMaterial() == Material.WATER ||
                chunk.getBlockState(pos.up()).getMaterial() == Material.WATER ||
                chunk.getBlockState(pos.east()).getMaterial() == Material.WATER ||
                chunk.getBlockState(pos.west()).getMaterial() == Material.WATER ||
                chunk.getBlockState(pos.south()).getMaterial() == Material.WATER ||
                chunk.getBlockState(pos.north()).getMaterial() == Material.WATER) {
            return;
        }

        IBlockState state = Blocks.AIR.getDefaultState();
        if (y < 11) {
            state = Blocks.LAVA.getDefaultState();
        }

        if (y < heightAt && state == Blocks.LAVA.getDefaultState()) {
            boolean nearWater = false;
            for (BlockPos neighbor : new BlockPos[] {
                    pos, pos.up(), pos.down(), pos.east(), pos.west(), pos.north(), pos.south()
            }) {
                if (chunk.getBlockState(neighbor).getMaterial() == Material.WATER) {
                    nearWater = true;
                    break;
                }
            }

            if (nearWater) {
                return;
            }
        }

        chunk.setBlockState(pos, state);

        if (state.getBlock() == Blocks.LAVA) {
            chunk.getWorld().scheduleBlockUpdate(pos, Blocks.LAVA, 0);
        }
    }

    private void sampleNoiseColumn(double[] buffer, int x, int z) {
        double offset = offsetNoise.generateNoiseOctaves(x / 128.0, 5423.434, z / 128.0, 1, 1, 1)[0] * 5.45;
        Random random = new Random(((long) x << 1) * 341873128712L + ((long) z << 1) * 132897987541L);

        if (random.nextInt(24) == 0) {
            offset += 4.0 + random.nextDouble() * 6;
        }

        for (int y = 0; y < buffer.length; y++) {
            buffer[y] = sampleNoise(x, y, z) + getFalloff(offset, y);
        }
    }

    private double sampleNoise(int x, int y, int z) {
        double noise = 0;
        double amplitude = 1;

        for (int i = 0; i < 6; i++) {
            double frequency = Math.pow(2, i);
            noise += caveNoise.generateNoiseOctaves(
                    x * 2.63 * amplitude,
                    y * 12.18 * amplitude,
                    z * 2.63 * amplitude,
                    1, 1, 1
            )[0] / amplitude;

            amplitude /= 2.0;
        }

        noise /= 1.25;

        double scale = (scaleNoise.generateNoiseOctaves(x / 96.0, y / 96.0, z / 96.0, 1, 1, 1)[0] + 0.2) * 30;
        noise += Math.min(scale, 0);

        return noise;
    }

    private double getFalloff(double offset, int y) {
        double falloffScale = 21.5 + offset;
        double falloff = Math.max((falloffScale / y), 0);
        falloff += Math.max((falloffScale / ((8) - y)), 0);
        double scaledY = y + 10.0;
        falloff = (1.5 * falloff) - (0.1 * scaledY * scaledY) - (-4.0 * y);
        return falloff;
    }

    @Override
    protected void recursiveGenerate(World world, int chunkX, int chunkZ, int originalX, int originalZ, Chunk chunk) {
        this.generate(world, chunkX, chunkZ, chunk);
    }
}