package bmu;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.MathHelper;

public class TransporterLock {
    public TileEntityTransporter transporter;
    public ChunkCoordinates target;
    public int ticksLocked = 0;

    public TransporterLock(TileEntityTransporter transporter, ChunkCoordinates target) {
        this.transporter = transporter;
        this.target = target;
    }

    public int getDistance() {
        return Math.abs(transporter.xCoord - target.posX)
             + Math.abs(transporter.yCoord - target.posY)
             + Math.abs(transporter.zCoord - target.posZ);
    }

    public int getBaseSignalRequired() {
        int manhattanDistance = getDistance();
        int minSignal = MathHelper.ceiling_double_int(
            CommonProxy.transporterBaseSignal * CommonProxy.beaconMultiplier);
        int signalRequired = MathHelper.ceiling_double_int(
            manhattanDistance * CommonProxy.signalPerBlock);

        return Math.max(minSignal, signalRequired);
    }

    public int getBaseSignalProduced() {
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

    //
}
