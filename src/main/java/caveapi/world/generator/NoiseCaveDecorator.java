package caveapi.world.generator;

import caveapi.noise.OpenSimplexNoise;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class NoiseCaveDecorator implements IWorldGenerator
{
    private static final float CAVE_NOISE_SCALE = 0.02f;
    private static final float DECORATION_CHANCE = 0.1f;
    private static final float FERN_CHANCE = 0.05f;
    private static final float GRASS_CHANCE = 0.3f;
    private static final float VINE_CHANCE = 0.15f;
    private static final int MIN_Y = 10;
    private static final int MAX_Y = 54;

    @Override
    public void generate(final Random rand, final int chunkX, final int chunkZ, final World world,
                         final IChunkGenerator generator, final IChunkProvider provider) {
        if (world.provider.getDimension() != 0) {
            return;

        }
        final int worldX = chunkX * 16;
        final int worldZ = chunkZ * 16;
        final long seed = world.getSeed();

        this.decorateCaves(rand, world, worldX, worldZ, seed);
    }

    private void decorateCaves(final Random rand, final World world, final int worldX, final int worldZ, final long seed) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = MIN_Y; y < MAX_Y; ++y) {
                    final BlockPos pos = new BlockPos(worldX + x, y, worldZ + z);

                    // 使用与NosieCaveGen相同的噪声算法检测洞穴位置
                    final double noiseX = (worldX + x) * CAVE_NOISE_SCALE;
                    final double noiseY = y * CAVE_NOISE_SCALE;
                    final double noiseZ = (worldZ + z) * CAVE_NOISE_SCALE;
                    final float caveNoise = OpenSimplexNoise.noise3_ImproveXY(seed, noiseX, noiseY, noiseZ);

                    // 如果当前位置是洞穴（空气方块）
                    if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
                        this.decorateCaveBlock(rand, world, pos, caveNoise);
                    }
                }
            }
        }
    }

    private void decorateCaveBlock(final Random rand, final World world, final BlockPos pos, final float caveNoise) {
        // 检查下方是否是固体方块（可作为植物基底）
        final BlockPos below = pos.down();
        final Block blockBelow = world.getBlockState(below).getBlock();

        // 检查是否是墙壁位置（一侧是空气，一侧是固体）
        final boolean isWall = this.isWallPosition(world, pos);

        if (blockBelow != Blocks.AIR && rand.nextFloat() < DECORATION_CHANCE) {
            // 在地面上生成植物
            this.generateGroundDecoration(rand, world, pos);
        } else if (isWall && rand.nextFloat() < VINE_CHANCE) {
            // 在墙壁上生成藤蔓
            this.generateWallDecoration(rand, world, pos);
        }
    }

    private boolean isWallPosition(final World world, final BlockPos pos) {
        // 检查六个方向，如果是墙壁则至少有一个方向是固体方块
        int solidNeighbors = 0;
        int airNeighbors = 0;

        for (final EnumFacing facing : EnumFacing.values()) {
            final BlockPos neighborPos = pos.offset(facing);
            final Block neighborBlock = world.getBlockState(neighborPos).getBlock();

            if (neighborBlock == Blocks.AIR) {
                airNeighbors++;
            } else {
                solidNeighbors++;
            }
        }

        // 如果是墙壁，应该既有固体邻居也有空气邻居
        return solidNeighbors > 0 && airNeighbors > 0;
    }

    private void generateGroundDecoration(final Random rand, final World world, final BlockPos pos) {
        final float randVal = rand.nextFloat();

        if (randVal < FERN_CHANCE) {
            // 生成蕨类植物
            final IBlockState fernLower = Blocks.DOUBLE_PLANT.getDefaultState()
                    .withProperty((IProperty)BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.FERN)
                    .withProperty((IProperty)BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.LOWER);
            final IBlockState fernUpper = Blocks.DOUBLE_PLANT.getDefaultState()
                    .withProperty((IProperty)BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.FERN)
                    .withProperty((IProperty)BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.UPPER);

            if (world.isAirBlock(pos) && world.isAirBlock(pos.up())) {
                world.setBlockState(pos, fernLower, 2);
                world.setBlockState(pos.up(), fernUpper, 2);
            }
        } else if (randVal < GRASS_CHANCE) {
            // 生成高草
            final IBlockState grassLower = Blocks.DOUBLE_PLANT.getDefaultState()
                    .withProperty((IProperty)BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.GRASS)
                    .withProperty((IProperty)BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.LOWER);
            final IBlockState grassUpper = Blocks.DOUBLE_PLANT.getDefaultState()
                    .withProperty((IProperty)BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.GRASS)
                    .withProperty((IProperty) BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.UPPER);

            if (world.isAirBlock(pos) && world.isAirBlock(pos.up())) {
                world.setBlockState(pos, grassLower, 2);
                world.setBlockState(pos.up(), grassUpper, 2);
            }
        } else {
            // 生成普通草丛
            world.setBlockState(pos, Blocks.TALLGRASS.getStateFromMeta(1), 2);
        }
    }

    private void generateWallDecoration(final Random rand, final World world, final BlockPos pos) {
        // 在墙壁上生成藤蔓
        world.setBlockState(pos, Blocks.VINE.getDefaultState(), 2);

        // 有几率向下延伸藤蔓
        BlockPos vinePos = pos.down();
        int maxLength = 3 + rand.nextInt(4); // 藤蔓长度3-6格

        for (int i = 0; i < maxLength; i++) {
            if (world.isAirBlock(vinePos) && this.hasSolidNeighbor(world, vinePos)) {
                world.setBlockState(vinePos, Blocks.VINE.getDefaultState(), 2);
                vinePos = vinePos.down();
            } else {
                break;
            }
        }
    }

    private boolean hasSolidNeighbor(final World world, final BlockPos pos) {
        for (final EnumFacing facing : EnumFacing.values()) {
            if (facing != EnumFacing.DOWN) { // 藤蔓不需要下方有支撑
                final BlockPos neighborPos = pos.offset(facing);
                if (world.getBlockState(neighborPos).getBlock() != Blocks.AIR) {
                    return true;
                }
            }
        }
        return false;
    }
}
