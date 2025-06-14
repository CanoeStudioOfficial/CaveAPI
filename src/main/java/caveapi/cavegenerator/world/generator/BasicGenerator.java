package caveapi.cavegenerator.world.generator;

import caveapi.cavegenerator.data.ConditionSettings;
import caveapi.cavegenerator.model.Conditions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.lang.ref.WeakReference;
import java.util.Objects;

public abstract class BasicGenerator {

    protected static final IBlockState BLK_AIR = Blocks.AIR.getDefaultState();
    protected static final IBlockState BLK_STONE = Blocks.STONE.getDefaultState();
    protected static final IBlockState BLK_WATER = Blocks.WATER.getDefaultState();

    protected final Conditions conditions;
    protected final WeakReference<World> world;

    public BasicGenerator(ConditionSettings conditions, World world) {
        Objects.requireNonNull(world, "Nullable world types are not yet supported.");
        this.conditions = Conditions.compile(conditions, world);
        this.world = new WeakReference<>(world);
    }

    protected final World getWorld() {
        return Objects.requireNonNull(world.get(), "World reference has been culled.");
    }

    public void generate(PrimerContext ctx) {
        final int dim = ctx.world.provider.getDimension();
        if (conditions.dimensions.test(dim)) {
            final Biome b = ctx.world.getBiome(new BlockPos(ctx.offsetX, 0, ctx.offsetZ));
            if (conditions.biomes.test(b)) {
                generateChecked(ctx);
            }
        }
    }

    protected abstract void generateChecked(PrimerContext ctx);

}
