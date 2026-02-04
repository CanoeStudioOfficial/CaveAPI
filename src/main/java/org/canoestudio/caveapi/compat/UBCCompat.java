package org.canoestudio.caveapi.compat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import org.canoestudio.caveapi.api.CaveBiome;

import java.lang.reflect.Method;

/**
 * Compatibility layer for Underground Biomes (UBC).
 */
public class UBCCompat implements IModCompat {
    private static boolean ubcLoaded = false;
    private static Method getUBCStoneMethod = null;

    @Override
    public void setup() {
        ubcLoaded = Loader.isModLoaded("undergroundbiomes");
        
        if (ubcLoaded) {
            UniversalCompatHandler.registerRemapper((world, pos, currentState, type) -> {
                if (type == 0) { // Wall
                    IBlockState ubcStone = getUBCStone(world, pos);
                    if (ubcStone != null) return ubcStone;
                }
                return currentState;
            });
        }
    }

    // Removed createUBCCompatibleBiome as it's now handled by the universal remapper

    /**
     * Gets the UBC stone state for a given position if UBC is loaded.
     * Otherwise returns null.
     */
    public static IBlockState getUBCStone(World world, BlockPos pos) {
        if (!ubcLoaded || getUBCStoneMethod == null) return null;
        try {
            return (IBlockState) getUBCStoneMethod.invoke(null, world, pos);
        } catch (Exception e) {
            return null;
        }
    }
}
