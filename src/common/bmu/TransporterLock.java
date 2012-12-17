package bmu;

import java.util.List;
import java.util.Random;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

import net.minecraftforge.common.ForgeDirection;

public class TransporterLock {
    public TileEntityTransporter transporter;
    public ChunkCoordinates target;
    public List<TileEntityInterdictor> rangingInterdictors;
    public int ticksLocked = 0;
    public int badLockTimer = 0;
    public double lockVariance = 0.0D;

    public TransporterLock(TileEntityTransporter transporter, ChunkCoordinates target) {
        this.transporter = transporter;
        this.target = target;
        this.rangingInterdictors = TransporterRegistry.interdictorsRangingOn(transporter.worldObj,
                target.posX, target.posY, target.posZ, transporter.frequency);
    }

    public int getDistance() {
        return Math.abs(transporter.xCoord - target.posX)
             + Math.abs(transporter.yCoord - target.posY)
             + Math.abs(transporter.zCoord - target.posZ);
    }

    public boolean isBeaconPresent() {
        World world = transporter.worldObj;
        int x = target.posX;
        int y = target.posY;
        int z = target.posZ;

        // A beacon applies if present on the target spot...
        if(world.getBlockId(x, y, z) == CommonProxy.beaconBlock.blockID) {
            TileEntityBeacon beacon = (TileEntityBeacon)world.getBlockTileEntity(x, y, z);
            if(beacon.frequency == transporter.frequency) {
                return true;
            }
        }

        // ...or any of the target's six neighbours.
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            x = target.posX + dir.offsetX;
            y = target.posY + dir.offsetY;
            z = target.posZ + dir.offsetZ;

            int blockID = world.getBlockId(x, y, z);
            if(blockID == CommonProxy.beaconBlock.blockID) {
                TileEntityBeacon beacon = (TileEntityBeacon)world.getBlockTileEntity(x, y, z);
                if(beacon.frequency == transporter.frequency) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getBaseSignalRequired() {
        // Base signal required only accounts for distance.
        int manhattanDistance = getDistance();
        int minSignal = MathHelper.ceiling_double_int(
            CommonProxy.transporterBaseSignal * CommonProxy.beaconMultiplier);
        int signalRequired = MathHelper.ceiling_double_int(
            manhattanDistance * CommonProxy.signalPerBlock);

        return Math.max(minSignal, signalRequired);
    }

    public int getBaseSignalProduced() {
        // Base signal produced only counts the transporter and its boost.
        int signal = CommonProxy.transporterBaseSignal;
        if(transporter.isBoosting()) {
            int boostEU = transporter.getBoostAmount();
            double boostEURatio = boostEU / CommonProxy.transporterBoostEU;
            double boostRatio = Math.log10(1 + (boostEURatio * 9));

            signal += MathHelper.ceiling_double_int(
                boostRatio * CommonProxy.transporterBoostMultiplier * getBaseSignalRequired());
        }

        return signal;
    }

    public int getEffectiveSignalProduced() {
        // Take into account interdictors
        double signal = getBaseSignalProduced();
        int dictorCount = 0;
        for(TileEntityInterdictor dictor : rangingInterdictors) {
            if(dictor.block(rangingInterdictors.size())) {
                signal *= CommonProxy.interdictorSignalRatios[dictorCount];
                dictorCount++;
            }
        }

        return MathHelper.ceiling_double_int(signal);
    }

    public int getEffectiveSignalRequired() {
        // Take into account beacons
        double signal = getBaseSignalRequired();
        if(isBeaconPresent()) {
            signal *= CommonProxy.beaconMultiplier;
        }

        return MathHelper.ceiling_double_int(signal);
    }

    public double getEffectiveSignalStrength() {
        double signalProduced = getEffectiveSignalProduced();
        double signalRequired = getEffectiveSignalRequired();

        double ratio = signalProduced / signalRequired;

        if(ratio > 1.0D) {
            ratio = 1.0D;
        }

        return ratio;
    }

    public double getSignalStrength() {
        // This is what the transporter should be asking for.
        double signal = getEffectiveSignalStrength();

        double decayFactor = 1.0D - ((double)ticksLocked / (double)CommonProxy.lockDuration);
        if(decayFactor < 0.0D) {
            decayFactor = 0.0D;
        }
        double decayRatio = Math.log10(1 + (decayFactor * 9));

        signal *= decayFactor;
        signal += lockVariance;

        if(signal > 1.0D) {
            signal = 1.0D;
        }
        else if(signal < 0.0D) {
            signal = 0.0D;
        }

        return signal;
    }

    public void signalTick() {
        lockVariance = transporter.worldObj.rand.nextGaussian()
            * CommonProxy.lockVariance * getEffectiveSignalStrength();

        ticksLocked++;

        if(getSignalStrength() < CommonProxy.lockThresholds[2]) {
            badLockTimer++;
        }
        else if(badLockTimer > 0) {
            badLockTimer = 0;
        }
    }

    public int getExtraCooldown() {
        if(badLockTimer == 0) {
            return 0;
        }

        double badLockRatio = badLockTimer / CommonProxy.lockLossDuration;
        return MathHelper.floor_double(badLockRatio * CommonProxy.lockLossCooldown);
    }
}
