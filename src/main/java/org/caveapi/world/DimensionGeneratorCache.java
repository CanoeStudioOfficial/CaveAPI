package org.caveapi.world;

import org.caveapi.world.feature.PillarGenerator;
import org.caveapi.world.feature.StalactiteGenerator;
import org.caveapi.world.feature.StructureGenerator;
import org.caveapi.world.generator.*;

import java.util.HashMap;
import java.util.List;


public class DimensionGeneratorCache {

    public final HashMap<Integer, List<TunnelGenerator>> tunnelsCache = new HashMap<>();
    public final HashMap<Integer, List<RavineGenerator>> ravinesCache = new HashMap<>();
    public final HashMap<Integer, List<CavernGenerator>> cavernsCache = new HashMap<>();
    public final HashMap<Integer, List<CavernGenerator>> deferredCavernsCache = new HashMap<>();
    public final HashMap<Integer, List<BurrowGenerator>> burrowsCache = new HashMap<>();
    public final HashMap<Integer, List<LayerGenerator>> layersCache = new HashMap<>();
    public final HashMap<Integer, List<LayerGenerator>> deferredLayersCache = new HashMap<>();
    public final HashMap<Integer, List<TunnelConnector<CavernGenerator>>> cavernTunnelsCache = new HashMap<>();
    public final HashMap<Integer, List<TunnelConnector<BurrowGenerator>>> burrowTunnelsCache = new HashMap<>();
    public final HashMap<Integer, List<StalactiteGenerator>> stalactitesCache = new HashMap<>();
    public final HashMap<Integer, List<PillarGenerator>> pillarsCache = new HashMap<>();
    public final HashMap<Integer, List<StructureGenerator>> structuresCache = new HashMap<>();

}
