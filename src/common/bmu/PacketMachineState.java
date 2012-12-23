package bmu;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class PacketMachineState {
    public TileEntityBMU machine;

    public PacketMachineState(TileEntityBMU machine) {
        this.machine = machine;
    }

    public Packet getPacket250() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);

        try {
            data.writeByte(CommonProxy.PACKET_MACHINE_STATE);
            data.writeInt(machine.xCoord);
            data.writeInt(machine.yCoord);
            data.writeInt(machine.zCoord);
            machine.writeToNetwork(data);
        }
        catch(IOException e) {
            // completely ignore it.
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = CommonProxy.CHANNEL_NAME;
        packet.data = bytes.toByteArray();
        packet.length = bytes.size();
        packet.isChunkDataPacket = true;

        return packet;
    }

    public static void readPacket250(ByteArrayDataInput data) throws IOException {
        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();

        World world = BeamMeUp.proxy.getClientWorld();
        assert world != null: "How did you manage to get a machine state packet on the server?!?";

        TileEntity te = world.getBlockTileEntity(x, y, z);
        if(!(te instanceof TileEntityBMU)) {
            return; // I don't know wtf to do to you!
        }

        ((TileEntityBMU)te).readFromNetwork(data);
        world.markBlockForRenderUpdate(x, y, z);
    }
}
