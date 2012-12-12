package bmu;

import ic2.api.Direction;
import ic2.api.EnergyNet;
import ic2.api.IEnergySink;
import ic2.api.IEnergyStorage;

import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class TileEntityInterdictor extends TileEntityBMU implements IEnergySink, IEnergyStorage {
    public static final int ENERGY_CAPACITY = 600000;
    public static final int MAX_ACCEPTED_EU = 128;
    public int energyStored = 0;
    public boolean isOnEnergyNet = false;

    public TileEntityInterdictor() {
        super(CommonProxy.bmuBlock);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if(!isOnEnergyNet) {
            EnergyNet.getForWorld(worldObj).addTileEntity(this);
            isOnEnergyNet = true;
        }
    }

    @Override
    public void invalidate() {
        if(isOnEnergyNet) {
            EnergyNet.getForWorld(worldObj).removeTileEntity(this);
            isOnEnergyNet = false;
        }
        super.invalidate();
    }

    @Override
    public String getDebugMessage() {
        return "Interdictor has " + energyStored + " EU inside.";
    }

    public void dropItem(ItemStack item) {
        EntityItem drop = new EntityItem(worldObj,
            (double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D, item);
        drop.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(drop);
    }

//public interface IEnergyStorage {
    public int getStored() {
        return energyStored;
    }

    public int getCapacity() {
        return ENERGY_CAPACITY;
    }

    public int getOutput() {
        return 0;
    }
//}

//public interface IEnergySink extends IEnergyAcceptor {
    public boolean demandsEnergy() {
        return energyStored < ENERGY_CAPACITY;
    }

    public int injectEnergy(Direction directionFrom, int amount) {
        if(amount > MAX_ACCEPTED_EU) {
            dropItem(CommonProxy.interdictorStack.copy());
            worldObj.setBlockAndMetadataWithNotify(xCoord, yCoord, zCoord, 0, 0);
        }
        energyStored += amount;
        return 0;
    }
//}

//public interface IEnergyAcceptor extends IEnergyTile {
    public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction)  {
        return true;
    }
//}

//public interface IEnergyTile {
    public boolean isAddedToEnergyNet() {
        return isOnEnergyNet;
    }
//}

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("energyStored", energyStored);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyStored = tag.getInteger("energyStored");
    }
}
