package org.canoestudio.caveapi.core;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.canoestudio.caveapi.Tags;
import org.canoestudio.caveapi.core.transformer.ChunkGeneratorOverworldTransformer;
import org.canoestudio.caveapi.core.transformer.ChunkPrimerTransformer;
import org.canoestudio.caveapi.core.transformer.ChunkProviderClientTransformer;
import org.canoestudio.caveapi.core.transformer.ChunkTransformer;
import org.canoestudio.caveapi.core.transformer.EntityTransformer;
import org.canoestudio.caveapi.core.transformer.ExtendedBlockStorageTransformer;
import org.canoestudio.caveapi.core.transformer.PlayerChunkMapTransformer;
import org.canoestudio.caveapi.core.transformer.SPacketChunkDataTransformer;
import org.canoestudio.caveapi.core.transformer.WorldProviderTransformer;
import org.canoestudio.caveapi.core.transformer.WorldTransformer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name(Tags.MOD_NAME + " Plugin")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001)
public final class CoremodHandler implements IFMLLoadingPlugin
{
    @SuppressWarnings("unused")
    public static final class Transformer implements IClassTransformer
    {
        @Nonnull
        public static final Multimap<String, IClassTransformer> TRANSFORMERS = MultimapBuilder.hashKeys().arrayListValues().build();
        static {
            TRANSFORMERS.put("net.minecraft.world.chunk.Chunk", new ChunkTransformer());
            TRANSFORMERS.put("net.minecraft.world.World", new WorldTransformer());
            TRANSFORMERS.put("net.minecraft.entity.Entity", new EntityTransformer());
            TRANSFORMERS.put("net.minecraft.network.play.server.SPacketChunkData", new SPacketChunkDataTransformer());
            TRANSFORMERS.put("net.minecraft.world.WorldProvider", new WorldProviderTransformer());
            TRANSFORMERS.put("net.minecraft.world.chunk.storage.ExtendedBlockStorage", new ExtendedBlockStorageTransformer());
            TRANSFORMERS.put("net.minecraft.server.management.PlayerChunkMap", new PlayerChunkMapTransformer());
            TRANSFORMERS.put("net.minecraft.client.multiplayer.ChunkProviderClient", new ChunkProviderClientTransformer());
            TRANSFORMERS.put("net.minecraft.world.chunk.ChunkPrimer", new ChunkPrimerTransformer());
            TRANSFORMERS.put("net.minecraft.world.gen.ChunkGeneratorOverworld", new ChunkGeneratorOverworldTransformer());
        }

        @Nullable
        @Override
        public byte[] transform(@Nullable final String name, @Nullable final String transformedName, @Nullable final byte[] basicClass) {
            return transformedName == null || basicClass == null ? basicClass : TRANSFORMERS.get(transformedName).stream()
                    .reduce(basicClass, (bc, ct) -> ct.transform(name, transformedName, bc), (bcOld, bcNew) -> bcNew);
        }
    }

    @Nonnull
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {"org.canoestudio.caveapi.core.CoremodHandler$Transformer"};
    }

    @Nullable
    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(@Nonnull final Map<String, Object> data) {
        // NO-OP
    }

    @Nullable
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
