package bmu;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

public class EventHandler {
    public Logger getLogger() {
        return FMLCommonHandler.instance().getFMLLogger();
    }

    @ForgeSubscribe
    public void onWorldUnload(WorldEvent.Unload event) {
        TransporterRegistry.clear(event.world);
        getLogger().log(Level.INFO, "BeamMeUp: Cleared transporter registry.");
    }
}
