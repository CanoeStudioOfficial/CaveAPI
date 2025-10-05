package com.personthecat.cavegenerator.world.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldDataProvider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

    private final WorldData data;

    public WorldDataProvider(WorldData capability) {
        this.data = capability;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == WorldDataHandler.CG_WORLD_CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == WorldDataHandler.CG_WORLD_CAP) {
            return WorldDataHandler.CG_WORLD_CAP.cast(this.data);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.data.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.data.deserializeNBT(nbt);
    }
}
