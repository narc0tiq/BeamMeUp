package bmu;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.NBTTagCompound;

public class ChunkCoordinatesBMU extends ChunkCoordinates {
    public ChunkCoordinatesBMU() {
        super();
    }

    public ChunkCoordinatesBMU(ChunkCoordinates coordinates) {
        super(coordinates);
    }

    public ChunkCoordinatesBMU(int x, int y, int z) {
        super(x, y, z);
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("x", posX);
        tag.setInteger("y", posY);
        tag.setInteger("z", posZ);
    }

    public void readFromNBT(NBTTagCompound tag) {
        posX = tag.getInteger("x");
        posY = tag.getInteger("y");
        posZ = tag.getInteger("z");
    }
}
