package org.canoestudio.caveapi.core;

public final class HeightConfig {
    public static final int MIN_HEIGHT = -64;
    public static final int MAX_HEIGHT = 320;
    public static final int TOTAL_HEIGHT = MAX_HEIGHT - MIN_HEIGHT;
    public static final int CUBE_COUNT = TOTAL_HEIGHT >> 4;
    public static final int CUBE_SIZE = 16;
    
    public static int blockToCube(int blockY) {
        return blockY >> 4;
    }
    
    public static int blockToLocal(int blockY) {
        return blockY & 0xF;
    }
    
    public static int cubeToMinBlock(int cubeY) {
        return cubeY << 4;
    }
    
    public static int heightToStorageIndex(int y) {
        return (y - MIN_HEIGHT) >> 4;
    }
    
    public static int storageIndexToMinHeight(int index) {
        return (index << 4) + MIN_HEIGHT;
    }
    
    public static boolean isYValid(int y) {
        return y >= MIN_HEIGHT && y < MAX_HEIGHT;
    }
}
