package bmu;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class ClientEventHandler extends EventHandler {

    @SideOnly(Side.CLIENT)
    public File extractAndLoadSound(Minecraft mc, String filename) throws Exception {
        File resourcesDir = new File(mc.mcDataDir, "resources/bmu/");
        if(!resourcesDir.exists()) {
            resourcesDir.mkdir();
        }

        File resourceFile = new File(resourcesDir, filename);
        if(!resourceFile.exists()) {
            InputStream in = BeamMeUp.class.getResourceAsStream("/bmu-sound/" + filename);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(resourceFile));

            byte[] buffer = new byte[1024];
            int length = 0;

            while((length = in.read(buffer)) >= 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
        return resourceFile;
    }

    @SideOnly(Side.CLIENT)
    @ForgeSubscribe
    public void onSoundLoad(SoundLoadEvent event) {
        String[] soundFiles = new String[] { "portal1.ogg", "portal2.ogg", "portal3.ogg" };

        Minecraft mc = Minecraft.getMinecraft();
        for(int i = 0; i < soundFiles.length; i++) {
            try {
                File f = extractAndLoadSound(mc, soundFiles[i]);
                event.manager.soundPoolSounds.addSound("bmu/" + soundFiles[i], f);
                getLogger().log(Level.INFO, "BeamMeUp: Loaded sound: " + soundFiles[i]);
            }
            catch(Exception e) {
                getLogger().log(Level.SEVERE, "BeamMeUp: Failed to load sound: " + soundFiles[i], e);
            }
        }
    }
}
