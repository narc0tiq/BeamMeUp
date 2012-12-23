package bmu;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TeleportLogic {
    public World world;

    public TeleportLogic(World world) {
        this.world = world;
    }

    public void moveBlocks(int srcX, int srcY, int srcZ, int dstX, int dstY, int dstZ) {
        for(int i = 0; i < 2; i++) {
            // Read source data
            int blockID   = world.getBlockId(srcX, srcY + i, srcZ);
            int meta      = world.getBlockMetadata(srcX, srcY + i, srcZ);
            TileEntity te = world.getBlockTileEntity(srcX, srcY + i, srcZ);
            NBTTagCompound tag = null;

            if(te != null) {
                tag = new NBTTagCompound();
                te.writeToNBT(tag);
            }
            System.out.println("Side: " + BeamMeUp.getSide() + " Read blockID: " + blockID + " and meta: " + meta + " Tile Entity: " + te + " Tag: " + tag);

            // Write destination data
            if(world.getBlockId(dstX, dstY + i, dstZ) == 0) {
                world.setBlockAndMetadata(dstX, dstY + i, dstZ, blockID, meta);
                if(tag != null) {
                    TileEntity newTE = TileEntity.createAndLoadEntity(tag);
                    world.setBlockTileEntity(dstX, dstY + i, dstZ, newTE);
                }

                // Clear source when teleport successful
                world.removeBlockTileEntity(srcX, srcY + i, srcZ);
                world.setBlock(srcX, srcY + i, srcZ, 0);
            }
        }

        // Separate step for marking blocks dirty, to prevent things like torches falling off mid-teleport
        for(int i = 0; i < 2; i++) {
            world.notifyBlockChange(srcX, srcY + i, srcZ, world.getBlockId(srcX, srcY + i, srcZ));
            world.notifyBlockChange(dstX, dstY + i, dstZ, world.getBlockId(dstX, dstY + i, dstZ));
        }
    }
}
