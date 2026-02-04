package org.canoestudio.caveapi.caveregister;

import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import org.canoestudio.caveapi.api.IOreStoneRegistry;

import java.util.Random;

/**
 * Adapter to ensure ores generate correctly in custom cave stones.
 */
public class OreGenerationAdapter {
    /**
     * Attempts to generate an ore vein that adapts to the local stone type.
     */
    public static void generateOreVein(World world, Random rand, BlockPos pos, Block oreBlock, int count) {
        Block baseStone = world.getBlockState(pos).getBlock();
        IOreStoneRegistry registry = OreStoneRegistryImpl.getInstance();
        
        Block adaptiveOre = registry.getOreInStone(oreBlock, baseStone);
        
        // Use WorldGenMinable with a matcher for the local stone type
        WorldGenMinable generator = new WorldGenMinable(adaptiveOre.getDefaultState(), count, BlockMatcher.forBlock(baseStone));
        generator.generate(world, rand, pos);
    }
}
