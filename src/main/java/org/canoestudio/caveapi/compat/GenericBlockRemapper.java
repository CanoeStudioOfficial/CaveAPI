package org.canoestudio.caveapi.compat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

/**
 * A generic remapper that uses simple block-to-block mappings.
 * Can be used to quickly add compatibility for mods with simple stone replacements.
 */
public class GenericBlockRemapper implements IBlockRemapper {
    private final Map<IBlockState, IBlockState> mappings = new HashMap<>();
    private final Map<String, String> modIdReplacements = new HashMap<>();

    /**
     * Add a specific block state mapping.
     */
    public void addMapping(IBlockState original, IBlockState replacement) {
        mappings.put(original, replacement);
    }

    /**
     * Add a rule: if mod 'modId' is loaded, and the original block is 'targetBlockId',
     * replace it with 'replacementBlockId'.
     */
    public void addModRule(String modId, String targetBlockId, String replacementBlockId) {
        if (ModCompatManager.isModLoaded(modId)) {
            Block target = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(targetBlockId));
            Block replacement = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(replacementBlockId));
            if (target != null && replacement != null) {
                addMapping(target.getDefaultState(), replacement.getDefaultState());
            }
        }
    }

    @Override
    public IBlockState remap(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, IBlockState currentState, int type) {
        return mappings.getOrDefault(currentState, currentState);
    }
}
