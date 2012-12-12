package bmu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

public class TransporterRegistry {
    // Key: frequency, Value: list of transporters on that frequency.
    public static Map<Integer, List<TileEntityTransporter>> transporters =
        new HashMap<Integer, List<TileEntityTransporter>>();
    public static List<TileEntityInterdictor> interdictors =
        new ArrayList<TileEntityInterdictor>();

    public static void registerTransporter(int frequency, TileEntityTransporter transporter) {
        List<TileEntityTransporter> freqTransporters = transporters.get(frequency);
        if(freqTransporters == null) {
            freqTransporters = new ArrayList<TileEntityTransporter>();
        }

        freqTransporters.add(transporter);
        transporters.put(frequency, freqTransporters);
    }

    public static void unregisterTransporter(int frequency, TileEntityTransporter transporter) {
        List<TileEntityTransporter> freqTransporters = transporters.get(frequency);
        if(freqTransporters == null) {
            return; // you were never registered on this frequency, go away.
        }

        freqTransporters.remove(transporter);

        if(freqTransporters.isEmpty()) {
            transporters.remove(frequency);
        }
    }

    public static List<TileEntityTransporter> transportersOnFrequency(int frequency) {
        return ImmutableList.copyOf(transporters.get(frequency));
    }

    public static void registerInterdictor(TileEntityInterdictor interdictor) {
        if(interdictors.contains(interdictor)) {
            return; // I already have you, bro.
        }
        interdictors.add(interdictor);
    }

    public static void unregisterInterdictor(TileEntityInterdictor interdictor) {
        interdictors.remove(interdictor);
    }

    public static List<TileEntityInterdictor> interdictorsRangingOn(int x, int y, int z) {
        List<TileEntityInterdictor> list = new ArrayList<TileEntityInterdictor>();
        for(TileEntityInterdictor interdictor: interdictors) {
            if((!interdictor.isInvalid()) && (interdictor.rangesOn(x, y, z))) {
                list.add(interdictor);
            }
        }
        return ImmutableList.copyOf(list);
    }
}
