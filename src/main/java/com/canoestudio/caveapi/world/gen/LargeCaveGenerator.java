package com.canoestudio.caveapi.world.gen;


import com.canoestudio.caveapi.core.CaveAPIConfig;
import com.canoestudio.caveapi.world.gen.ICaveGenerator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import java.util.Random;

public class LargeCaveGenerator implements ICaveGenerator {
    private static final int MAX_CAVES = 3;
    private final Random rand;
    private final long seed;

    public LargeCaveGenerator(long seed) {
        this.seed = seed;
        this.rand = new Random(seed);
    }

    @Override
    public void generate(World world, ChunkPrimer primer, int chunkX, int chunkZ) {
        if (world.getSeed() != seed) {
            rand.setSeed(world.getSeed() ^ seed);
        }

        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int i = 0; i < MAX_CAVES; i++) {
            double startX = baseX + rand.nextInt(16);
            double startY = CaveAPIConfig.minY + rand.nextInt(CaveAPIConfig.maxY - CaveAPIConfig.minY);
            double startZ = baseZ + rand.nextInt(16);

            int tunnels = rand.nextInt(3) + 1;
            for (int t = 0; t < tunnels; t++) {
                generateTunnel(world, primer, startX, startY, startZ);
            }
        }
    }

    private void generateTunnel(World world, ChunkPrimer primer, double x, double y, double z) {
        double angleXZ = rand.nextDouble() * Math.PI * 2;
        double angleY = (rand.nextDouble() - 0.5) * Math.PI / 2;
        double slope = MathHelper.clamp(rand.nextDouble(), 0.75, 1.0);

        double curX = x;
        double curY = y;
        double curZ = z;

        int length = rand.nextInt(100) + 50;
        double radius = 3 + rand.nextDouble() * 4;

        for (int i = 0; i < length; i++) {
            double curve = Math.sin(i * Math.PI / length) * radius * 1.5;
            double currentRadius = radius + curve;

            int minX = (int)(curX - currentRadius) - 1;
            int minY = (int)(curY - currentRadius) - 1;
            int minZ = (int)(curZ - currentRadius) - 1;
            int maxX = (int)(curX + currentRadius) + 1;
            int maxY = (int)(curY + currentRadius) + 1;
            int maxZ = (int)(curZ + currentRadius) + 1;

            // 在区块内生成
            if (minX <= 15 && maxX >= 0 && minZ <= 15 && maxZ >= 0) {
                for (int px = Math.max(minX, 0); px <= Math.min(maxX, 15); px++) {
                    double dx = px - curX;
                    for (int pz = Math.max(minZ, 0); pz <= Math.min(maxZ, 15); pz++) {
                        double dz = pz - curZ;
                        for (int py = Math.max(minY, CaveAPIConfig.minY); py <= Math.min(maxY, CaveAPIConfig.maxY - 1); py++) {
                            double dy = py - curY;

                            double distSq = dx*dx + dy*dy*2 + dz*dz;
                            if (distSq < currentRadius*currentRadius) {
                                // 保留墙壁（10%概率）
                                if (distSq > (currentRadius-1)*(currentRadius-1) && rand.nextDouble() < 0.1) {
                                    continue;
                                }

                                primer.setBlockState(px, py, pz, CaveAPIConfig.getCaveAirState());
                            }
                        }
                    }
                }
            }

            // 移动位置
            curX += Math.sin(angleXZ) * Math.cos(angleY);
            curZ += Math.cos(angleXZ) * Math.cos(angleY);
            curY += Math.sin(angleY);

            // 随机改变方向
            if (rand.nextDouble() < 0.02) {
                angleXZ += (rand.nextDouble() - 0.5) * 0.8;
            }
            if (rand.nextDouble() < 0.02) {
                angleY = MathHelper.clamp(angleY + (rand.nextDouble() - 0.5) * 0.4, -Math.PI/3, Math.PI/3);
            }
            if (rand.nextDouble() < 0.02 && radius > 1) {
                radius *= 0.9;
            }
            if (rand.nextDouble() < 0.02 && radius < 8) {
                radius *= 1.1;
            }

            // 应用斜率
            angleXZ *= slope;
            angleY *= slope;
        }
    }
}