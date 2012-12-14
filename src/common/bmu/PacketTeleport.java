package bmu;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;

public class PacketTeleport {
    public int sourceX;
    public int sourceY;
    public int sourceZ;
    public int destinationX;
    public int destinationY;
    public int destinationZ;

    public String playerName;

    public PacketTeleport(int sourceX, int sourceY, int sourceZ, int destinationX, int destinationY, int destinationZ, String playerName) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.sourceZ = sourceZ;
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.destinationZ = destinationZ;
        this.playerName = playerName;
    }

    public Packet getPacket250() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);

        try {
            data.writeByte(CommonProxy.PACKET_TELEPORT);
            data.writeInt(sourceX);
            data.writeInt(sourceY);
            data.writeInt(sourceZ);
            data.writeInt(destinationX);
            data.writeInt(destinationY);
            data.writeInt(destinationZ);
            data.writeUTF(playerName);
        }
        catch(IOException e) {
            // completely ignore it.
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = CommonProxy.CHANNEL_NAME;
        packet.data = bytes.toByteArray();
        packet.length = bytes.size();
        packet.isChunkDataPacket = false;

        return packet;
    }

    public static void readPacket250(ByteArrayDataInput data) throws IOException {
        int sourceX = data.readInt();
        int sourceY = data.readInt();
        int sourceZ = data.readInt();

        int destinationX = data.readInt();
        int destinationY = data.readInt();
        int destinationZ = data.readInt();

        String playerName = data.readUTF();

        World world = BeamMeUp.proxy.getClientWorld();
        assert world != null: "How did you manage to get a teleport packet on the server?!?";
        String thisPlayer = BeamMeUp.proxy.getPlayerName();

        if(thisPlayer.equals(playerName)) {
            Minecraft.getMinecraft().sndManager.playSoundFX("bmu.portal", 1.0F, 1.0F);
        }
        else {
            world.playSound(sourceX, sourceY, sourceZ, "bmu.portal", 1.0F, 1.0F);
            world.playSound(destinationX, destinationY, destinationZ, "bmu.portal", 1.0F, 1.0F);
        }
    }
}
