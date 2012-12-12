package bmu;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import net.minecraftforge.common.ForgeDirection;

public class BlockBMU extends BlockContainer {
    public static final int DATA_TRANSPORTER = 0;
    public static final int DATA_INTERDICTOR = 1;
    public static final int DATA_BEACON      = 2;

    public static final int TEX_TRANSPORTER_TOP    =   0;
    public static final int TEX_TRANSPORTER_BOTTOM =   1;
    public static final int TEX_TRANSPORTER_SIDE   =   2;

    public static final int TEX_INTERDICTOR_TOP    =  16;
    public static final int TEX_INTERDICTOR_BOTTOM =  17;
    public static final int TEX_INTERDICTOR_SIDE   =  18;

    public static final int TEX_BEACON_ON          =  32;
    public static final int TEX_BEACON_OFF         =  33;

    public static final int TEX_INVALID            = 255;

    public BlockBMU(int blockID) {
        super(blockID, Material.iron);

        this.setBlockName("bmu.block.generic");
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setTextureFile(ClientProxy.BLOCKS_PNG);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(int id, CreativeTabs type, List tabContents) {
        tabContents.add(CommonProxy.transporterStack);
        tabContents.add(CommonProxy.interdictorStack);
        tabContents.add(CommonProxy.beaconStack);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int data) {
        if(data == DATA_TRANSPORTER) {
            return new TileEntityTransporter((Block)this);
        }
        else if(data == DATA_INTERDICTOR) {
            return new TileEntityInterdictor((Block)this);
        }
        else if(data == DATA_BEACON) {
            return new TileEntityBeacon((Block)this);
        }

        return null;
    }

    @Override
    public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        int data = world.getBlockMetadata(x, y, z);
        if(data == DATA_BEACON) {
            // Query it and return on or off, depending on state
            return TEX_BEACON_ON;
        }
        return getBlockTextureFromSideAndMetadata(side, data);
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int data) {
        ForgeDirection face = ForgeDirection.getOrientation(side);

        if(data == DATA_TRANSPORTER) {
            switch(face) {
                case UP:   return TEX_TRANSPORTER_TOP;
                case DOWN: return TEX_TRANSPORTER_BOTTOM;
                default:   return TEX_TRANSPORTER_SIDE;
            }
        }
        else if(data == DATA_INTERDICTOR) {
            switch(face) {
                case UP:   return TEX_INTERDICTOR_TOP;
                case DOWN: return TEX_INTERDICTOR_BOTTOM;
                default:   return TEX_INTERDICTOR_SIDE;
            }
        }

        return TEX_INVALID;
    }
}
