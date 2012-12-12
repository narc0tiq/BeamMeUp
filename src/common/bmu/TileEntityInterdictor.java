package bmu;

import dan200.computer.api.IPeripheral;
import dan200.computer.api.IComputerAccess;

import ic2.api.Direction;
import ic2.api.EnergyNet;
import ic2.api.IEnergySink;
import ic2.api.IEnergyStorage;

import net.minecraft.src.Block;
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
        if(!isInitialized) {
            EnergyNet.getForWorld(worldObj).addTileEntity(this);
            TransporterRegistry.registerInterdictor(this);
            isInitialized = true;
        }

        if(!isEnabled) {
            return;
        }

        if(energyStored > 4) {
            energyStored -= 4; // Minimal consumption, even when idle.
        }
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
        if(!isEnabled) {
            return false;
        }

        int maxRange = CommonProxy.maxInterdictorRange;

        return ((Math.abs(x - xCoord) <= maxRange)
             && (Math.abs(y - yCoord) <= maxRange)
             && (Math.abs(z - zCoord) <= maxRange));
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
        return isInitialized;
    }
//}

//public interface IPeripheral {
    public String getType() {
        return "bmu.transporter";
    }

    public String[] getMethodNames() {
        return peripheralMethods;
    }

    public synchronized Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
        if(peripheralMethods[method] == "getFrequency") {
            return getPeripheralFrequency(arguments);
        }
        else if(peripheralMethods[method] == "setFrequency") {
            return setPeripheralFrequency(arguments);
        }
        else if(peripheralMethods[method] == "getEnergyLevel") {
            return getPeripheralEnergy(arguments);
        }
        else if(peripheralMethods[method] == "enable") {
            return peripheralEnable(arguments);
        }
        else if(peripheralMethods[method] == "disable") {
            return peripheralDisable(arguments);
        }
        else if(peripheralMethods[method] == "isEnabled") {
            return isPeripheralEnabled(arguments);
        }
        else if(peripheralMethods[method] == "rangesOn") {
            return peripheralRangesOn(arguments);
        }

        throw new Exception("That's not a valid method for me!");
    }

    public boolean canAttachToSide(int side) {
        return true;
    }

    public void attach(IComputerAccess computer, String computerSide) {}
    public void detach(IComputerAccess computer) {}
//}

    public Object[] getPeripheralFrequency(Object[] arguments) throws Exception {
        return new Object[] { this.frequency };
    }

    public Object[] setPeripheralFrequency(Object[] arguments) throws Exception {
        if((arguments.length < 1) || (!(arguments[0] instanceof Double))) {
            throw new Exception("Invalid arguments: Need a single number.");
        }

        if(this.frequency == ((Double)arguments[0]).intValue()) {
            return new Object[] { false };
        }

        this.frequency = ((Double)arguments[0]).intValue();
        return new Object[] { true };
    }

    public Object[] getPeripheralEnergy(Object[] arguments) throws Exception {
        return new Object[] { this.energyStored };
    }

    public Object[] peripheralEnable(Object[] arguments) throws Exception {
        if(this.isEnabled) {
            return new Object[] { false };
        }

        this.isEnabled = true;
        return new Object[] { true };
    }

    public Object[] peripheralDisable(Object[] arguments) throws Exception {
        if(!this.isEnabled) {
            return new Object[] { false };
        }

        this.isEnabled = false;
        return new Object[] { true };
    }

    public Object[] isPeripheralEnabled(Object[] arguments) throws Exception {
        return new Object[] { this.isEnabled };
    }

    public Object[] peripheralRangesOn(Object[] arguments) throws Exception {
        if((arguments.length < 1)
                || (!(arguments[0] instanceof Double))
                || (!(arguments[1] instanceof Double))
                || (!(arguments[2] instanceof Double))) {
            throw new Exception("Invalid arguments: Need three numbers.");
        }

        int x = ((Double)arguments[0]).intValue();
        int y = ((Double)arguments[1]).intValue();
        int z = ((Double)arguments[2]).intValue();

        return new Object[] { this.rangesOn(x, y, z) };
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("energyStored", energyStored);
        tag.setInteger("frequency",    frequency);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyStored = tag.getInteger("energyStored");
        frequency    = tag.getInteger("frequency");
    }
}
