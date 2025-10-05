package org.caveapi.world.roads;

import org.caveapi.data.RoadSettings;
import org.caveapi.model.Conditions;
import org.caveapi.world.data.WorldData;
import org.caveapi.world.data.WorldDataHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.lang.ref.WeakReference;
import java.util.*;

public class RoadGenerator {

    public static List<RoadLockedNodes> lockedNodes = new ArrayList<>();
    public static List<RoadNode> outRoadList = new ArrayList<>();
    public static HashMap<Integer, RoadNode> nodeList = new HashMap<>();
    public static TreeMap<Float, Integer> priorityList = new TreeMap<>();

    protected final Conditions conditions;
    protected final WeakReference<World> world;
    private final RoadSettings cfg;

    /*
    public static int lastX;
    public static int lastZ;
    public static int lastIndex;
     */

    public static int targX;
    public static int targZ;

    public static int startX;
    public static int startZ;

    public static int currentIndex = 0;
    public static float currentAdd = 0f;
    private static final IBlockState BLK_WATER = Blocks.WATER.getDefaultState();

    public static int[][] heightmap = new int[25][25];

    // distance lookup table
    // feels kinda wasteful somehow
    private static final float[] distTable = new float[1250];

    static {
        for (int i = 0; i < 1250; i++) {
            distTable[i] = (float)Math.sqrt(i*2);
        }
    }

    public RoadGenerator(RoadSettings cfg, World world) {
        Objects.requireNonNull(world, "Nullable world types are not yet supported.");
        this.conditions = Conditions.compile(cfg.conditions, world);
        this.world = new WeakReference<>(world);
        this.cfg = cfg;
    }



    public static float getDist(int x, int z) {
        int d = Math.abs(x - targX + z - targZ);
        if (d < 1250) {
            return distTable[d];
        } else {
            return 65536f;
        }
    }


    public static void setHeightmap(World world, int chunkX, int chunkZ) {
        for (int cX = 0; cX < 5; cX++) {
            for (int cZ = 0; cZ < 5; cZ++) {
              Chunk chunk = world.getChunk(chunkX, chunkZ);
              for (int bX = 0; bX < 5; bX++) {
                  for (int bZ = 0; bZ < 5; bZ++) {
                      heightmap[bX][bZ] = chunk.getHeightValue(bX*3, bZ*3);
                      // if the top block is water, no road go here
                      // TODO: make this configurable?
                      if (chunk.getBlockState(bX*3, heightmap[bX][bZ], bZ*3).equals(BLK_WATER)) {
                          heightmap[bX][bZ] = -1;
                      }
                  }
              }
           }
        }
    }


    public static void startRoad(World world, int chunkX, int chunkZ) {
        setHeightmap(world, chunkX, chunkZ);
        currentIndex = 0;
        currentAdd = 0f;
        lockedNodes.clear();
        lockedNodes.add(currentIndex, new RoadLockedNodes());

        // Get actual start/end points
        int x = 12;
        int z = 10;
        targX = 12;
        targZ = 24;

        // initial node
        RoadNode node = new RoadNode(x, z, 0, getDist(x, z), 0, null);

        RoadNode currentNode = node;
        while (true) {
            if (   currentNode.x + 1 >= targX
                && currentNode.x - 1 <= targX
                && currentNode.z + 1 >= targZ
                && currentNode.z - 1 <= targZ ) {
                // STOP THE SEARCH
                break;
            }
            checkNode(currentNode);
            final int priority = priorityList.firstEntry().getValue();
            currentNode = nodeList.get(priority);
        }

        // get the final list of nodes
        outRoadList.clear();
        while(currentNode.parent != null) {
            outRoadList.add(currentNode.parent);
            currentNode = currentNode.parent;
        }
        for (RoadNode n : outRoadList) {
                        
        }
    }

    public static void checkNode(RoadNode node) {
        addCardinalNodes(node);
    }

    public static void addCardinalNodes(RoadNode node) {
        addNodeToList(node.x+1, node.z, 1, node);
        addNodeToList(node.x, node.z+1, 1, node);
        addNodeToList(node.x-1, node.z,  1, node);
        addNodeToList(node.x, node.z-1,  1, node);
        addNodeToList(node.x+1, node.z+1,  2, node);
        addNodeToList(node.x-1, node.z+1,  2, node);
        addNodeToList(node.x+1, node.z-1,  2, node);
        addNodeToList(node.x-1, node.z-1,  2, node);
    }

    public static void addNodeToList(int x, int z, int type, RoadNode prevNode) {
        // do nothing
        if (x < 0 || x > 24 || z < 0 || z > 24)
            return;
        // TODO: make configurable
        if (heightmap[x][z] >= 63 && !lockedNodes.get(prevNode.index).array[x][z]) { // sea level
            int deltaHeight = Math.abs(heightmap[x][z] - heightmap[prevNode.x][prevNode.z]);
            RoadNode node;
            switch (type) {
                case 1: // straight
                    node = new RoadNode(x, z, prevNode.cost + 1f, getDist(x, z), prevNode.index, prevNode);
                    node.cost += (float) (deltaHeight * deltaHeight);
                    break;
                case 2: // diagonal
                    node = new RoadNode(x, z, prevNode.cost + 1.4f, getDist(x, z), prevNode.index, prevNode);
                    node.cost += (float) (deltaHeight * deltaHeight);
                    break;
                case 3: // bridge/tunnel start
                    currentIndex++;
                    lockedNodes.add(currentIndex, new RoadLockedNodes(lockedNodes.get(prevNode.index).array));
                    node = new RoadNode(x, z, prevNode.cost, getDist(x, z), currentIndex, prevNode);
                    // TODO: make configurable
                    final int add = 20;
                    int d = Math.abs(x - prevNode.x + z - prevNode.z) + add;
                    node.cost += d;
                    break;
                default: // should never happen
                    node = new RoadNode(0, 0, 0, 0, 0, prevNode);
                    break;
            }
            // if the height difference is great, do not lock the node
            int key = nodeToInt(x, z, node.index);
            if (deltaHeight < 4) {
                lockedNodes.get(prevNode.index).array[x][z] = true;
            }
            // if there is already a node here (as a result of not being locked above), check which has the lower cost before replacing
            if (nodeList.containsKey(key)) {
                if (nodeList.get(key).cost > node.cost)
                    nodeList.put(key, node);
            } else {
                nodeList.put(key, node);
            }
            currentAdd += 1f/1000f;
            priorityList.put((node.dist + node.cost + currentAdd), key);
        } else {
            lockedNodes.get(prevNode.index).array[x][z] = true;
        }
    }

    public static int nodeToInt(int x, int z, int index) {
        return x + z*25 + index*625;
    }

    /**
     * @param nodes must be a 5x5 array of booleans where true is a node and false is not
     * @return booleans converted into an integer bitmask
     */
    public static int nodeListToInt(boolean[][] nodes) {
        int mult = 1;
        int out = 0;
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                if(nodes[x][z]) {
                    out+=mult;
                }
                mult*=2;
            }
        }
        return out;
    }

    public static boolean[][] nodeListFromInt(int i) {
        int mult = 16777216; // 2^24
        boolean[][] nodes = new boolean[5][5];
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                if(i-mult > 0) {
                    nodes[x][z]=true;
                }
                i-=mult;
                mult/=2;
            }
        }
        return nodes;
    }

    /*
    public static void intToNode(int in) {
        lastX = in%25;
        lastZ = (in/25)%25;
        lastIndex = (in/625)%25;
    }
     */

    public static void setChunkNodes(World world, int x, int z, int nodeInt) {
        getChunkCap(world, x, z).getData().setInteger("r", nodeInt);
        getChunk(world, x, z).markDirty();
    }

    public static int getChunkNodes(World world, int x, int z) {
        WorldData data = getChunkCap(world, x, z);
        return (int)data.getData().getInteger("r");
    }

    public static boolean getChunkHasNodes(World world, int x, int z) {
        WorldData data = getChunkCap(world, x, z);
        return (int)data.getData().getInteger("r") > 0;
    }


    private static WorldData getWorldCap(World world) {
        return world.getCapability(WorldDataHandler.CG_WORLD_CAP, null);
    }

    private static WorldData getChunkCap(World world, int x, int z) {
        return getChunk(world, x, z).getCapability(WorldDataHandler.CG_WORLD_CAP, null);
    }

    private static Chunk getChunk(World world, int x, int z) {
        return world.getChunk(x, z);
    }

}
