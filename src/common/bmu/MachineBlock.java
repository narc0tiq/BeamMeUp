package bmu;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.util.Icon;
import net.minecraft.client.renderer.texture.IconRegister;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MachineBlock extends BlockContainer {
    public MachineBlock(int blockID) {
        super(blockID, Material.iron);

        this.setUnlocalizedName(BlockInfo.MACHINE_DEFAULT_KEY);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(int blockID, CreativeTabs tab, List tabContents) {
        // TODO: if(!tabContents.Contains(blah))...
        tabContents.add(new ItemStack(blockID, 1, 0));
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return null; // Dang it, BlockContainer. Your class is insufficient.
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        // TODO: if(meta == TRANSPORTER_META)...
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegistry) {
        blockIcon = BlockIcons.TELEPORTER_SIDE.register(iconRegistry);
    }

}
