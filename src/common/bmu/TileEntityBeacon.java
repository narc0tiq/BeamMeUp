package bmu;

import com.google.common.io.ByteArrayDataInput;

import java.io.DataOutputStream;

import net.minecraft.block.Block;

public class TileEntityBeacon extends TileEntityBMU {
    public int frequency = 1337;

    public TileEntityBeacon() {
        super(CommonProxy.bmuBlock);
    }

    @Override
    public void writeToNetwork(DataOutputStream data) {
    }

    @Override
    public void readFromNetwork(ByteArrayDataInput data) {
    }
}
