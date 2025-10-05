package com.personthecat.cavegenerator.world.roads;

import com.personthecat.cavegenerator.world.data.WorldData;
import com.personthecat.cavegenerator.world.data.WorldDataHandler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class RoadNode {

    public int x; // multiply by 3 for real X coordinate
    public int z; // multiply by 3 for real Z coordinate
    public float cost;
    public float dist;
    public int index;
    public RoadNode parent;

    public RoadNode(int xIn, int zIn, float costIn, float distIn, int indexIn, RoadNode parentIn) {
        x = xIn;
        z = zIn;
        cost = costIn;
        dist = distIn;
        index = indexIn;
        parent = parentIn;
    }

}
