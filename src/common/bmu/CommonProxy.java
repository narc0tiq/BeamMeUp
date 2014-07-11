package bmu;

import java.util.logging.Level;

import net.minecraftforge.common.Configuration;

public class CommonProxy {
    public void preInit() {


    }

    public void init(Configuration config) {
        // TODO: Init config stuff here.

        if(config.hasChanged())
            config.save();
    }

}
