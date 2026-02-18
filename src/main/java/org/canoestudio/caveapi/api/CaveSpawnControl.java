package org.canoestudio.caveapi.api;

/**
 * Control logic for cave spawns, specifically to optimize surface spawning.
 * Provides an API to prevent underground mobs from filling the mob cap while players are exploring the surface.
 */
public class CaveSpawnControl {
    private static boolean restrictUndergroundSpawnsWhenOnSurface = false;
    private static int surfaceHeightThreshold = 60; // Typically around sea level (63)
    private static double checkRadius = 128.0; // Standard spawn radius

    /**
     * Enable or disable the restriction of underground spawns when players are on the surface.
     * When enabled, if a player is on the surface, underground mob spawns near them will be denied.
     * 
     * @param enable True to enable the restriction, false to disable (default).
     */
    public static void setRestrictUndergroundSpawns(boolean enable) {
        restrictUndergroundSpawnsWhenOnSurface = enable;
    }

    /**
     * @return Whether the restriction is currently enabled.
     */
    public static boolean isRestrictUndergroundSpawns() {
        return restrictUndergroundSpawnsWhenOnSurface;
    }

    /**
     * Set the Y-level threshold for what is considered "surface".
     * Players above this level who can see the sky are considered "on the surface".
     * Mobs below this level who cannot see the sky are considered "underground".
     * 
     * @param threshold The Y-level threshold (default 60).
     */
    public static void setSurfaceHeightThreshold(int threshold) {
        surfaceHeightThreshold = threshold;
    }

    public static int getSurfaceHeightThreshold() {
        return surfaceHeightThreshold;
    }

    /**
     * Set the radius to check for nearby players.
     * 
     * @param radius The radius in blocks (default 128.0).
     */
    public static void setCheckRadius(double radius) {
        checkRadius = radius;
    }

    public static double getCheckRadius() {
        return checkRadius;
    }
}
