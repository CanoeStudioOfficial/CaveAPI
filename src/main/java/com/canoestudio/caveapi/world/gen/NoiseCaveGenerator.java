package com.canoestudio.caveapi.world.gen;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import java.util.Random;

public class NoiseCaveGenerator implements ICaveGenerator {
    private final NoiseGeneratorPerlin[] noiseGens;
    private final Random rand = new Random();

    public NoiseCaveGenerator(long seed) {
        noiseGens = new NoiseGeneratorPerlin[3];
        for (int i = 0; i < noiseGens.length; i++) {
            noiseGens[i] = new NoiseGeneratorPerlin(new Random(seed + i), 4);
        }
    }

    @Override
    public void generate(World world, ChunkPrimer primer, int chunkX, int chunkZ) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            double dx = (baseX + x) * CaveAPIConfig.NOISE_SCALE_X;
            for (int z = 0; z < 16; z++) {
                double dz = (baseZ + z) * CaveAPIConfig.NOISE_SCALE_Z;
                for (int y = CaveAPIConfig.MIN_Y; y < CaveAPIConfig.MAX_Y; y++) {
                    double dy = y * CaveAPIConfig.NOISE_SCALE_Y;

                    double noise = calculateCaveNoise(dx, dy, dz);

                    // 大型洞穴 (cheese caves)
                    if (noise > 0.8) {
                        carveLargeCave(primer, x, y, z, noise);
                    }
                    // 噪声洞穴 (noodle caves)
                    else if (noise > 0.2) {
                        carveNoiseCave(primer, x, y, z, noise);
                    }
                }
            }
        }
    }

    private double calculateCaveNoise(double x, double y, double z) {
        double noise = 0.0;
        double scale = 1.0;

        for (int i = 0; i < noiseGens.length; i++) {
            noise += noiseGens[i].getValue(x * scale, y * scale, z * scale) * (1.0 / scale);
            scale *= 2.0;
        }

        return noise * 0.5 + 0.5; // 标准化到[0,1]
    }

    private void carveLargeCave(ChunkPrimer primer, int x, int y, int z, double size) {
        int radius = (int)(size * 4) + 2;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double dist = Math.sqrt(dx*dx + dy*dy*2 + dz*dz); // 垂直方向压缩

                    if (dist < radius) {
                        int px = x + dx;
                        int py = y + dy;
                        int pz = z + dz;

                        if (px >= 0 && px < 16 &&
                                py >= CaveAPIConfig.MIN_Y && py < CaveAPIConfig.MAX_Y &&
                                pz >= 0 && pz < 16) {

                            // 概率性生成支撑柱
                            if (dist > radius - 1 && rand.nextDouble() < 0.05) {
                                continue;
                            }

                            primer.setBlockState(px, py, pz, CaveAPIConfig.CAVE_AIR);
                        }
                    }
                }
            }
        }
    }

    private void carveNoiseCave(ChunkPrimer primer, int x, int y, int z, double intensity) {
        // 简单噪声洞穴 - 单个方块
        primer.setBlockState(x, y, z, CaveAPIConfig.CAVE_AIR);

        // 20%概率扩展为小隧道
        if (rand.nextDouble() < 0.2 * intensity) {
            int dirX = rand.nextInt(3) - 1;
            int dirY = rand.nextInt(3) - 1;
            int dirZ = rand.nextInt(3) - 1;

            int length = rand.nextInt(3) + 1;

            for (int i = 0; i < length; i++) {
                int px = MathHelper.clamp(x + dirX * i, 0, 15);
                int py = MathHelper.clamp(y + dirY * i, CaveAPIConfig.MIN_Y, CaveAPIConfig.MAX_Y - 1);
                int pz = MathHelper.clamp(z + dirZ * i, 0, 15);

                primer.setBlockState(px, py, pz, CaveAPIConfig.CAVE_AIR);
            }
        }
    }
}