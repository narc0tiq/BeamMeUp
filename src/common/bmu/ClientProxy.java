package bmu;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
    public static final String BLOCKS_PNG = "/bmu-gfx/blocks.png";

    @Override
    public void preInit() {
        super.preInit();

        EventHandler eventHandler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }

    @Override
    public void init() {
        super.init();

        initTextures();
    }

    public void initTextures() {
        MinecraftForgeClient.preloadTexture(BLOCKS_PNG);
    }

    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }

    public String getPlayerName() {
        return FMLClientHandler.instance().getClient().thePlayer.username;
    }
}
