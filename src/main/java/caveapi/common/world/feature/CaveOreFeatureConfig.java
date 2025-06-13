package caveapi.common.world.feature;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class CaveOreFeatureConfig {

    public final List<Target> targets;
    public final int size;
    public final float discardChanceOnAirExposure;

    public CaveOreFeatureConfig(List<Target> targets, int size, float discardChanceOnAirExposure) {
        this.targets = targets;
        this.size = size;
        this.discardChanceOnAirExposure = discardChanceOnAirExposure;
    }

    public CaveOreFeatureConfig(List<Target> targets, int size) {
        this(targets, size, 0.0F);
    }

    public CaveOreFeatureConfig(RuleTest target, IBlockState state, int size, float discardChanceOnAirExposure) {
        this(ImmutableList.of(new Target(target, state)), size, discardChanceOnAirExposure);
    }

    public CaveOreFeatureConfig(RuleTest target, IBlockState state, int size) {
        this(ImmutableList.of(new Target(target, state)), size, 0.0F);
    }

    public static Target createTarget(RuleTest test, IBlockState state) {
        return new Target(test, state);
    }

    public static class Target {
        public final RuleTest target;
        public final IBlockState state;

        public Target(RuleTest target, IBlockState state) {
            this.target = target;
            this.state = state;
        }
    }

    public static abstract class RuleTest {
        public abstract boolean test(IBlockState state, Random rand);
    }

    public static final class Rules {
        public static final RuleTest BASE_STONE_OVERWORLD = new SimpleRuleTest(state ->
                state.getBlock().getMaterial(state) == Material.ROCK);

        public static final RuleTest STONE_ORE_REPLACEABLES = new SimpleRuleTest(state ->
                state.getBlock().getMaterial(state) == Material.ROCK || state.getBlock() == Blocks.DIRT);

        public static final RuleTest NETHERRACK = new BlockMatchRuleTest(Blocks.NETHERRACK);

        public static final RuleTest BASE_STONE_NETHER = new OreDictRuleTest("netherrack");

        public static final RuleTest END_STONE = new BlockMatchRuleTest(Blocks.END_STONE);
    }

    public static class SimpleRuleTest extends RuleTest {
        private final Predicate<IBlockState> predicate;

        public SimpleRuleTest(Predicate<IBlockState> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(IBlockState state, Random rand) {
            return predicate.test(state);
        }
    }

    public static class BlockMatchRuleTest extends RuleTest {
        private final Block block;

        public BlockMatchRuleTest(Block block) {
            this.block = block;
        }

        @Override
        public boolean test(IBlockState state, Random rand) {
            return state.getBlock() == block;
        }
    }

    public static class StateMatchRuleTest extends RuleTest {
        private final IBlockState matchState;

        public StateMatchRuleTest(IBlockState state) {
            this.matchState = state;
        }

        @Override
        public boolean test(IBlockState state, Random rand) {
            return state == matchState;
        }
    }

    public static class OreDictRuleTest extends RuleTest {
        private final String oreName;

        public OreDictRuleTest(String oreName) {
            this.oreName = oreName;
        }

        @Override
        public boolean test(IBlockState state, Random rand) {
            ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
            int[] oreIDs = OreDictionary.getOreIDs(stack);

            for (int id : oreIDs) {
                if (OreDictionary.getOreName(id).equals(oreName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class RandomRuleTest extends RuleTest {
        private final RuleTest baseTest;
        private final float chance;

        public RandomRuleTest(RuleTest baseTest, float chance) {
            this.baseTest = baseTest;
            this.chance = chance;
        }

        @Override
        public boolean test(IBlockState state, Random rand) {
            return baseTest.test(state, rand) && rand.nextFloat() < chance;
        }
    }
}