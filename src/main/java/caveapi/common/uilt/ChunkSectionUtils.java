package caveapi.common.uilt;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ChunkSectionUtils {
    // 手动计算区块坐标替代 SectionPos
    public static long sectionToLong(BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkY = pos.getY() >> 4;
        int chunkZ = pos.getZ() >> 4;
        return ((long) chunkX & 0xFFFFFFF) << 38 | ((long) chunkY & 0xFFF) << 26 | (chunkZ & 0xFFFFFFF);
    }

    // 使用 ExtendedBlockStorage 替代 ChunkSection
    public static ExtendedBlockStorage getSection(Chunk chunk, int yIndex) {
        ExtendedBlockStorage[] sections = chunk.getBlockStorageArray();
        if (sections[yIndex] == null) {
            sections[yIndex] = new ExtendedBlockStorage(yIndex << 4, true);
        }
        return sections[yIndex];
    }
}