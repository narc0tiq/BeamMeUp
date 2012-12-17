package bmu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.World;

public class TransporterRegistry {
    // Key: frequency, Value: list of transporters on that frequency.
    public static Map<World, Map<Integer, List<TileEntityTransporter>>> transporters =
        new HashMap<World, Map<Integer, List<TileEntityTransporter>>>();
    public static Map<World, Map<ChunkCoordinates, TileEntityInterdictor>> interdictors =
        new HashMap<World, Map<ChunkCoordinates, TileEntityInterdictor>>();

    public static void registerTransporter(int frequency, TileEntityTransporter transporter) {
        Map<Integer, List<TileEntityTransporter>> worldMap = transporters.get(transporter.worldObj);
        if(worldMap == null) {
            worldMap = new HashMap<Integer, List<TileEntityTransporter>>();
            transporters.put(transporter.worldObj, worldMap);
        }

        List<TileEntityTransporter> freqTransporters = worldMap.get(frequency);
        if(freqTransporters == null) {
            freqTransporters = new ArrayList<TileEntityTransporter>();
            worldMap.put(frequency, freqTransporters);
        }

        freqTransporters.add(transporter);
    }

    public static void unregisterTransporter(int frequency, TileEntityTransporter transporter) {
        Map<Integer, List<TileEntityTransporter>> worldMap = transporters.get(transporter.worldObj);
        if(worldMap == null) {
            worldMap = new HashMap<Integer, List<TileEntityTransporter>>();
            transporters.put(transporter.worldObj, worldMap);
        }

        List<TileEntityTransporter> freqTransporters = worldMap.get(frequency);
        if(freqTransporters == null) {
            return; // you were never registered on this frequency, go away.
        }

        freqTransporters.remove(transporter);
    }

    public static List<TileEntityTransporter> transportersOnFrequency(World world, int frequency) {
        return ImmutableList.copyOf(transporters.get(world).get(frequency));
    }

    public static void registerInterdictor(TileEntityInterdictor interdictor) {
        ChunkCoordinates location = new ChunkCoordinates(interdictor.xCoord, interdictor.yCoord, interdictor.zCoord);
        Map<ChunkCoordinates, TileEntityInterdictor> worldMap = interdictors.get(interdictor.worldObj);
        if(worldMap == null) {
            worldMap = new HashMap<ChunkCoordinates, TileEntityInterdictor>();
            interdictors.put(interdictor.worldObj, worldMap);
        }

        if(worldMap.containsKey(location)) {
            throw new RuntimeException("Interdictor registering itself with the same world and coordinates twice! Game over, man.");
        }
        worldMap.put(location, interdictor);
    }

    public static void unregisterInterdictor(TileEntityInterdictor interdictor) {
        ChunkCoordinates location = new ChunkCoordinates(interdictor.xCoord, interdictor.yCoord, interdictor.zCoord);
        Map<ChunkCoordinates, TileEntityInterdictor> worldMap = interdictors.get(interdictor.worldObj);
        if(worldMap == null) {
            worldMap = new HashMap<ChunkCoordinates, TileEntityInterdictor>();
            interdictors.put(interdictor.worldObj, worldMap);
        }
        worldMap.remove(location);
    }

    public static List<TileEntityInterdictor> interdictorsRangingOn(World world, int x, int y, int z, int frequency) {
        Map<ChunkCoordinates, TileEntityInterdictor> worldMap = interdictors.get(world);
        if(worldMap == null) {
            worldMap = new HashMap<ChunkCoordinates, TileEntityInterdictor>();
            interdictors.put(world, worldMap);
        }

        List<TileEntityInterdictor> list = new ArrayList<TileEntityInterdictor>();
        for(TileEntityInterdictor interdictor: worldMap.values()) {
            if((!interdictor.isInvalid()) && interdictor.rangesOn(x, y, z) && interdictor.blocks(frequency)) {
                list.add(interdictor);
                if(list.size() >= 4) {
                    break;
                }
            }
        }
        return ImmutableList.copyOf(list);
    }

    public static void clear(World world) {
        transporters.remove(world);
        interdictors.remove(world);
    }
}
