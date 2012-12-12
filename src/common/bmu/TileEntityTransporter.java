package bmu;

import ic2.api.Direction;
import ic2.api.EnergyNet;
import ic2.api.IEnergySink;
import ic2.api.IEnergyStorage;

import net.minecraft.src.Block;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class TileEntityTransporter extends TileEntityBMU implements IEnergySink, IEnergyStorage {
    public static final int ENERGY_CAPACITY = 10000000;
    public int energyStored = 0;
    public boolean isOnEnergyNet = false;

    public TileEntityTransporter() {
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
        return "Transporter has " + energyStored + " EU inside.";
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
