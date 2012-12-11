package bmu;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import net.minecraftforge.common.Configuration;

@Mod(
        modid = "BeamMeUp",
        version = "%conf:VERSION%",
        useMetadata = true,
        dependencies = "required-after:IC2;after:ComputerCraft"
    )
@NetworkMod(
        clientSideRequired = true,
        serverSideRequired = false,
        versionBounds = "%conf:VERSION_BOUNDS%"//,
//        channels = { CommonProxy.channelName },
//        packetHandler = PacketHandler.class
)
public class BeamMeUp {
    @Mod.Instance("BeamMeUp")
    public static BeamMeUp instance;

    @SidedProxy(clientSide = "bmu.ClientProxy", serverSide = "bmu.CommonProxy")
    public static CommonProxy proxy;

    public static Configuration config;

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
    }

    @Mod.Init
    public void init(FMLInitializationEvent event) {
        try {
            config.load();
        }
        catch (RuntimeException e) { /* and ignore it */ }
        proxy.init();
        config.save();
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event) {
        //loadIntegration("whatever");
    }

    @SuppressWarnings("unchecked")
    private static boolean loadIntegration(String name) {
        System.out.println("BeamMeUp: Loading " + name + " integration...");

        try {
            Class t = BeamMeUp.class.getClassLoader().loadClass("bmu.integration." + name);
            return ((Boolean)t.getMethod("init", new Class[0]).invoke((Object)null, new Object[0])).booleanValue();
        }
        catch (Throwable e) {
            System.out.println("BeamMeUp: Did not load " + name + " integration: " + e);
            return false;
        }
    }

    public static Side getSide() {
        return FMLCommonHandler.instance().getEffectiveSide();
    }
}
