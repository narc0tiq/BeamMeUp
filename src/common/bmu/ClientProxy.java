package bmu;

import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
    public static final String BLOCKS_PNG = "/bmu-gfx/blocks.png";

    @Override
    public void init() {
        super.init();

        initTextures();
    }

    public void initTextures() {
        MinecraftForgeClient.preloadTexture(BLOCKS_PNG);
    }
}
