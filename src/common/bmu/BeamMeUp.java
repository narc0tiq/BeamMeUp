package bmu;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

import cpw.mods.fml.common.network.NetworkMod;

import net.minecraftforge.common.Configuration;


@Mod(
    modid = BeamMeUp.MODID,
    version = BeamMeUp.VERSION,
    useMetadata = true,
    dependencies = "after:ComputerCraft"
)
@NetworkMod(
    clientSideRequired = true,
    serverSideRequired = false,
    versionBounds = "%conf:VERSION_BOUNDS%"
)
public class BeamMeUp {
    public static final String MODID = "%conf:PROJECT_NAME%";
    public static final String VERSION = "%conf:VERSION%";

    @Mod.Instance(BeamMeUp.MODID)
    public static BeamMeUp instance;

    @SidedProxy(clientSide = "bmu.ClientProxy", serverSide = "bmu.CommonProxy")
    public static CommonProxy proxy;

    public static Configuration config;
    public static Logger log;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());

        log = Logger.getLogger(MODID);
        log.setParent(FMLLog.getLogger());

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(config);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        //loadIntegration("whatever");
    }

    @SuppressWarnings("unchecked")
    private static boolean loadIntegration(String name) {
        log.info("Loading " + name + " integration...");

        try {
            Class t = BeamMeUp.class.getClassLoader().loadClass("bmu.integration." + name);
            return ((Boolean)t.getMethod("init", new Class[0]).invoke((Object)null, new Object[0])).booleanValue();
        }
        catch (Throwable e) {
            log.log(Level.WARNING, "Integration with " + name + " did not load!", e);
            return false;
        }
    }
}
