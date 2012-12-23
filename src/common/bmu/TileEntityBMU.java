package bmu;

import com.google.common.io.ByteArrayDataInput;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityBMU extends TileEntity {
    public TileEntityBMU(Block blockType) {
        this.blockType = blockType;
    }

    public String getDebugMessage() {
        return "";
    }

    // Helper function for CC peripheral integration
    public Object[] peripheralReturn(Object... args) {
        return args;
    }

    public boolean isActive() {
        return false;
    }

    @Override
    public Packet getDescriptionPacket() {
        return (new PacketMachineState(this)).getPacket250();
    }

    public abstract void readFromNetwork(ByteArrayDataInput data);
    public abstract void writeToNetwork(DataOutputStream data) throws IOException;
}
