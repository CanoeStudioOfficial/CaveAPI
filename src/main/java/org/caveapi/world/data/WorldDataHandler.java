package org.caveapi.world.data;

import org.caveapi.Main;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class WorldDataHandler {

    private static final ResourceLocation CG_WORLD_CAP_RL = new ResourceLocation(Main.MODID, "cgw");
    private static final ResourceLocation CG_CHUNK_CAP_RL = new ResourceLocation(Main.MODID, "cgc");

    @CapabilityInject(WorldData.class)
    public static Capability<WorldData> CG_WORLD_CAP = null;

    @SubscribeEvent
    public static void attachToWorld(AttachCapabilitiesEvent<World> event) {
        event.addCapability(CG_WORLD_CAP_RL, new WorldDataProvider(new WorldData()));
    }

    @SubscribeEvent
    public static void attachToChunk(AttachCapabilitiesEvent<Chunk> event) {
        event.addCapability(CG_CHUNK_CAP_RL, new WorldDataProvider(new WorldData()));
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(WorldData.class, new Capability.IStorage<WorldData>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<WorldData> capability, WorldData instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<WorldData> capability, WorldData instance, EnumFacing side, NBTBase nbt) {
                if (nbt instanceof NBTTagCompound) {
                    instance.deserializeNBT(((NBTTagCompound) nbt));
                }
            }
        }, WorldData::new);
    }

}
