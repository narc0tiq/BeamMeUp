package bmu;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;

public class TileEntityBMU extends TileEntity {
    public TileEntityBMU(Block blockType) {
        this.blockType = blockType;
    }

    public String getDebugMessage() {
        return "";
    }

    // Helper function for CC peripheral integration
    public Object[] peripheralReturn(Object... args) {
        return args;
    }
}
