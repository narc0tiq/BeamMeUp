package bmu;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;

public class TileEntityBMU extends TileEntity {
    public TileEntityBMU(Block blockType) {
        this.blockType = blockType;
    }
}
