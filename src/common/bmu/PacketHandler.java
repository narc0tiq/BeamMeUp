package bmu;

import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;

public class PacketHandler implements IPacketHandler {
    public void onPacketData(INetworkManager network, Packet250CustomPayload packet, Player player) {
        ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);

        try {
            byte packetID = data.readByte();

            if(packetID == CommonProxy.PACKET_TELEPORT) {
                PacketTeleport.readPacket250(data);
            }
            else {
                System.err.println("Narc, did you forget to add packet handling for packet ID " + packetID + "?");
            }
        }
        catch(IOException e) {
            // and pretend it never existed
        }
    }
}
