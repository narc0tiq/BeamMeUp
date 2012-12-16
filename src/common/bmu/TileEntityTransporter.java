package bmu;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.Side;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import dan200.computer.api.IPeripheral;
import dan200.computer.api.IComputerAccess;

import ic2.api.Direction;
import ic2.api.EnergyNet;
import ic2.api.IEnergySink;
import ic2.api.IEnergyStorage;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class TileEntityTransporter extends TileEntityBMU implements IEnergySink, IEnergyStorage, IPeripheral {
    public static final int ENERGY_CAPACITY = 10000000;
    public static final int TELEPORT_DURATION = 100; // ticks
    public int energyStored = 0;
    public int frequency = 0;
    public ChunkCoordinatesBMU target = new ChunkCoordinatesBMU();
    public TeleportLogic teleporter = null;
    public TransporterLock lock = null;
    public boolean isOnEnergyNet = false;
    public String[] peripheralMethods = new String[]{
        "getEnergyLevel",
        "getFrequency", "setFrequency",
        "acquireTransporterTarget", "setTargetCoordinates", "getTargetCoordinates",
        "acquireLock", "hasLock", "getLockStrength", "setBoostAmount", "releaseLock",
        "retrieve", "transmit"
    }; // the contract for these is public Object[] methodNamePeripheral(Object[] args) must exist

    public EntityPlayer player = null;
    public EntityTransporterHelper freezer = null;
    public int teleportTimer = TELEPORT_DURATION;
    public boolean retrieving = false;

    public TileEntityTransporter() {
        super(CommonProxy.bmuBlock);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if(BeamMeUp.getSide() == Side.CLIENT) {
            return; // client is a poopyhead
        }

        if(!isOnEnergyNet) {
            EnergyNet.getForWorld(worldObj).addTileEntity(this);
            isOnEnergyNet = true;
        }
        if(teleportTimer < TELEPORT_DURATION) { teleportTimer++; }

        if(teleportTimer == 50) {
            teleport();
        }

        if(teleportTimer >= TELEPORT_DURATION) {
            concludeTeleport();
        }
    }

    public boolean beginTeleport() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        if(teleporter == null) {
            teleporter = new TeleportLogic(worldObj);
        }

        teleportTimer = 0;

        int dimension = worldObj.getWorldInfo().getDimension();

        PacketTeleport packet = new PacketTeleport(xCoord, yCoord + 1, zCoord, target.posX, target.posY, target.posZ, "none");
        PacketDispatcher.sendPacketToAllAround(xCoord, yCoord + 1, zCoord, 16.0D, dimension, packet.getPacket250());
        if(target.getDistanceSquared(xCoord, yCoord + 1, zCoord) > ((double)(16 * 16))) {
            PacketDispatcher.sendPacketToAllAround(target.posX, target.posY, target.posZ, 16.0D, dimension, packet.getPacket250());
        }

        return true;
    }

    public void concludeTeleport() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        if(freezer != null) {
            player.unmountEntity(freezer);
            freezer.isDead = true;
            worldObj.removeEntity(freezer);
            freezer = null; // and let the garbage collector catch it.
        }
    }

    public boolean transmit() {
        retrieving = false;
        return beginTeleport();
    }

    public boolean retrieve() {
        retrieving = true;
        return beginTeleport();
    }

    public void teleport() {
        if(retrieving) {
            teleporter.moveBlocks(target.posX, target.posY, target.posZ, xCoord, yCoord + 1, zCoord);
        }
        else {
            teleporter.moveBlocks(xCoord, yCoord + 1, zCoord, target.posX, target.posY, target.posZ);
        }
    }

    public void freezePlayer(EntityPlayer player) {
        if(freezer != null) {
            return;
        }

        this.player = player;
        //freezeTimer = 100;
        freezer = new EntityTransporterHelper(worldObj);
        freezer.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, 0);
        freezer.noClip = true;
        worldObj.spawnEntityInWorld(freezer);
        player.mountEntity(freezer);

        int dimension = worldObj.getWorldInfo().getDimension();
        PacketTeleport packet = new PacketTeleport((int)player.posX, (int)player.posY, (int)player.posZ,
               xCoord + 50, yCoord + 6, zCoord + 30, player.username);
        PacketDispatcher.sendPacketToAllAround(player.posX, player.posY, player.posZ, 16.0D, dimension, packet.getPacket250());
        PacketDispatcher.sendPacketToAllAround(xCoord + 50, yCoord + 6, zCoord + 30, 16.0D, dimension, packet.getPacket250());
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

    public boolean isActive() {
        return (teleportTimer < TELEPORT_DURATION);
    }

    public boolean isBoosting() {
        return false;
    }

    public int getBoostAmount() {
        return 0;
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

//public interface IPeripheral {
    public String getType() {
        return "bmu.transporter";
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
    public Object[] getEnergyLevelPeripheral(Object[] arguments) {
        return peripheralReturn(energyStored);
    }

    public Object[] getFrequencyPeripheral(Object[] arguments) {
        return peripheralReturn(frequency);
    }

    public Object[] setFrequencyPeripheral(Object[] arguments) throws Exception {
        if((arguments.length < 1) || (!(arguments[0] instanceof Double))) {
           throw new Exception("Invalid arguments: Need a single number.");
        }
        if(this.frequency != ((Double)arguments[0]).intValue()) {
            this.frequency = ((Double)arguments[0]).intValue();
            return peripheralReturn(true);
        }

        return peripheralReturn(false);
    }

    public Object[] setTargetCoordinatesPeripheral(Object[] arguments) throws Exception {
        if((arguments.length < 1)
                || (!(arguments[0] instanceof Double))
                || (!(arguments[1] instanceof Double))
                || (!(arguments[2] instanceof Double))) {
            throw new Exception("Invalid arguments: Need three numbers.");
        }

        int x = ((Double)arguments[0]).intValue();
        int y = ((Double)arguments[1]).intValue();
        int z = ((Double)arguments[2]).intValue();

        if((target.posX == x) && (target.posY == y) && (target.posZ == z)) {
            return peripheralReturn(false);
        }
        else {
            target.set(x, y, z);
            return peripheralReturn(true);
        }
    }

    public Object[] getTargetCoordinatesPeripheral(Object[] arguments) {
        return peripheralReturn(target.posX, target.posY, target.posZ);
    }

    public Object[] retrievePeripheral(Object[] arguments) {
        return peripheralReturn(this.retrieve());
    }

    public Object[] transmitPeripheral(Object[] arguments) {
        return peripheralReturn(this.transmit());
    }
    //}

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("energyStored", energyStored);
        tag.setInteger("frequency", frequency);

        NBTTagCompound newTag = new NBTTagCompound("target");
        target.writeToNBT(newTag);
        tag.setCompoundTag("target", newTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyStored = tag.getInteger("energyStored");
        frequency = tag.getInteger("frequency");
        if(tag.hasKey("target")) {
            target.readFromNBT(tag.getCompoundTag("target"));
        }
    }

    @Override
    public void writeToNetwork(DataOutputStream data) throws IOException {
        data.writeInt(teleportTimer);
    }

    @Override
    public void readFromNetwork(ByteArrayDataInput data) {
        teleportTimer = data.readInt();
    }
}
