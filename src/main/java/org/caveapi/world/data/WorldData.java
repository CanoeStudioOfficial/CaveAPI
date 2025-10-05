package com.personthecat.cavegenerator.world.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class WorldData implements INBTSerializable<NBTTagCompound> {

    private NBTTagCompound data = new NBTTagCompound();

    public NBTTagCompound getData() {
        return this.data;
    }

    public void setData(NBTTagCompound data) {
        this.data = data;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.data;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.data = nbt;
    }

}
