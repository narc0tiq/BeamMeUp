package bmu;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.Side;

import dan200.computer.api.IPeripheral;
import dan200.computer.api.IComputerAccess;

import ic2.api.Direction;
import ic2.api.EnergyNet;
import ic2.api.IEnergySink;
import ic2.api.IEnergyStorage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import net.minecraft.src.Block;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityItem;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class TileEntityInterdictor extends TileEntityBMU implements IEnergySink, IEnergyStorage, IPeripheral {
    public static final int ENERGY_CAPACITY = 600000;
    public static final int MAX_ACCEPTED_EU = 128;
    public int energyStored = 0;
    public int frequency = 0; // one-item whitelist
    public boolean isInitialized = false;
    public boolean isEnabled = true;
    public boolean wasActive = false;
    public int consumeThisTick = 0;
    public String[] peripheralMethods = new String[]{
        "getEnergyLevel",
        "getFrequency", "setFrequency",
        "disable", "enable", "isEnabled",
        "rangesOn"
    };

    public TileEntityInterdictor() {
        super(CommonProxy.bmuBlock);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if(BeamMeUp.getSide() == Side.CLIENT) {
            return;
        }

        if(!isInitialized) {
            EnergyNet.getForWorld(worldObj).addTileEntity(this);
            TransporterRegistry.registerInterdictor(this);
            isInitialized = true;
        }

        if(!isEnabled) {
            return;
        }

        if(energyStored > CommonProxy.interdictorBaseConsumption) {
            energyStored -= CommonProxy.interdictorBaseConsumption; // Minimal consumption, even when idle.
            energyStored -= consumeThisTick;
            consumeThisTick = 0;
        }

        if(wasActive != this.isActive()) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        wasActive = this.isActive();
    }

    @Override
    public void invalidate() {
        if(isInitialized) {
            EnergyNet.getForWorld(worldObj).removeTileEntity(this);
            TransporterRegistry.unregisterInterdictor(this);
            isInitialized = false;
        }
        super.invalidate();
    }

    @Override
    public boolean isActive() {
        if(BeamMeUp.getSide() == Side.CLIENT) {
            return isEnabled;
        }
        else { // Side.SERVER and whatnot
            return (isEnabled && (energyStored > CommonProxy.interdictorBaseConsumption));
        }
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

    public boolean rangesOn(int x, int y, int z) {
        if(!isActive()) {
            return false;
        }

        int maxRange = CommonProxy.interdictorRange;

        return ((Math.abs(x - xCoord) <= maxRange)
             && (Math.abs(y - yCoord) <= maxRange)
             && (Math.abs(z - zCoord) <= maxRange));
    }

    public boolean blocks(int frequency) {
        if(!isActive()) {
            return false;
        }

        return (frequency != this.frequency);
    }

    public boolean block(int dictorCount) {
        int consumption = CommonProxy.interdictorDampeningConsumption[dictorCount - 1];

        if((energyStored - consumeThisTick) < consumption) {
            return false; // we didn't actually manage to interdict: no power
        }

        consumeThisTick += consumption;
        return true;
    }

    public boolean rangesOn(ChunkCoordinates target) {
        return rangesOn(target.posX, target.posY, target.posZ);
    }

//public interface IEnergyStorage {
    public int getStored() {
        return energyStored;
    }

    public void setStored(int amount) {
        energyStored = amount;
    }

    public int addEnergy(int amount) {
        energyStored += amount;
        return energyStored;
    }

    public int getCapacity() {
        return ENERGY_CAPACITY;
    }

    public int getOutput() {
        return 0;
    }

    public boolean isTeleporterCompatible(Direction side) {
        return false;
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
        return isInitialized;
    }
//}

//public interface IPeripheral {
    public String getType() {
        return "bmu.interdictor";
    }

    public String[] getMethodNames() {
        return peripheralMethods;
    }

    public synchronized Object[] callMethod(IComputerAccess computer, int index, Object[] arguments) throws Exception {
        Method method = null;
        try {
            method = this.getClass().getMethod(peripheralMethods[index] + "Peripheral", Object[].class);
        }
        catch(Exception e) {
            throw new Exception("Could not find method " + peripheralMethods[index] + ", something is seriously wrong!");
        }

        return (Object[])method.invoke(this, new Object[]{arguments});
    }

    public boolean canAttachToSide(int side) {
        return true;
    }

    public void attach(IComputerAccess computer, String computerSide) {}
    public void detach(IComputerAccess computer) {}
//}

// Actual peripheral methods {
    public Object[] getFrequencyPeripheral(Object[] arguments) throws Exception {
        return peripheralReturn(this.frequency);
    }

    public Object[] setFrequencyPeripheral(Object[] arguments) throws Exception {
        if((arguments.length < 1) || (!(arguments[0] instanceof Double))) {
            throw new Exception("Invalid arguments: Need a single number.");
        }

        if(this.frequency == ((Double)arguments[0]).intValue()) {
            return peripheralReturn(false);
        }

        this.frequency = ((Double)arguments[0]).intValue();
        return peripheralReturn(true);
    }

    public Object[] getEnergyLevelPeripheral(Object[] arguments) throws Exception {
        return peripheralReturn(this.energyStored);
    }

    public Object[] enablePeripheral(Object[] arguments) throws Exception {
        if(this.isEnabled) {
            return peripheralReturn(false);
        }

        this.isEnabled = true;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return peripheralReturn(true);
    }

    public Object[] disablePeripheral(Object[] arguments) throws Exception {
        if(!this.isEnabled) {
            return peripheralReturn(false);
        }

        this.isEnabled = false;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return peripheralReturn(true);
    }

    public Object[] isEnabledPeripheral(Object[] arguments) throws Exception {
        return peripheralReturn(this.isEnabled);
    }

    public Object[] rangesOnPeripheral(Object[] arguments) throws Exception {
        if((arguments.length < 1)
                || (!(arguments[0] instanceof Double))
                || (!(arguments[1] instanceof Double))
                || (!(arguments[2] instanceof Double))) {
            throw new Exception("Invalid arguments: Need three numbers.");
        }

        int x = ((Double)arguments[0]).intValue();
        int y = ((Double)arguments[1]).intValue();
        int z = ((Double)arguments[2]).intValue();

        return peripheralReturn(this.rangesOn(x, y, z));
    }
//}

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("energyStored", energyStored);
        tag.setInteger("frequency",    frequency);
        tag.setBoolean("active",       isEnabled);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyStored = tag.getInteger("energyStored");
        frequency    = tag.getInteger("frequency");
        isEnabled    = tag.getBoolean("active");
    }

    @Override
    public void writeToNetwork(DataOutputStream data) throws IOException {
        data.writeBoolean(isActive());
    }

    @Override
    public void readFromNetwork(ByteArrayDataInput data) {
        isEnabled = data.readBoolean();
    }
}
