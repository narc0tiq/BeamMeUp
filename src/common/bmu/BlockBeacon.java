package bmu;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockBeacon extends BlockBMU {
    public static final int TEX_BEACON_ON          =  32;
    public static final int TEX_BEACON_OFF         =  33;

    public BlockBeacon(int blockID) {
        super(blockID);
        this.setBlockName("bmu.block.beacon");
        this.setBlockBounds(0.35F, 0.0F, 0.35F, 0.65F, 0.6F, 0.65F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null; // walk-through
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 2; // torch render type
    }

    @Override
    public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        // Query it and return the off texture, if necessary
        return getBlockTextureFromSide(side);
    }

    @Override
    public int getBlockTextureFromSide(int side) {
        return TEX_BEACON_ON;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.doesBlockHaveSolidTopSurface(x, y - 1, z);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return createNewTileEntity(world, 0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int data) {
        return new TileEntityBeacon();
    }
}
