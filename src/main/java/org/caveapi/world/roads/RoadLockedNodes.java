package com.personthecat.cavegenerator.world.roads;

public class RoadLockedNodes {

    public boolean[][] array;

    public RoadLockedNodes() {
        array = new boolean[25][25];
    }

    public RoadLockedNodes(boolean[][] prev) {
        array = prev.clone();
    }
}
