package bmu;

import com.google.common.io.ByteArrayDataInput;

import java.io.DataOutputStream;

import net.minecraft.src.Block;

public class TileEntityBeacon extends TileEntityBMU {
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
