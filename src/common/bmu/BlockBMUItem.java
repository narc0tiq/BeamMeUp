package bmu;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;

public class BlockBMUItem extends ItemBlock {
    public BlockBMUItem(int i) {
        super(i);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getItemNameIS(ItemStack stack) {
        if(stack.isItemEqual(CommonProxy.transporterStack)) {
            return "bmu.block.transporter";
        }
        else if(stack.isItemEqual(CommonProxy.interdictorStack)) {
            return "bmu.block.interdictor";
        }
        else if(stack.isItemEqual(CommonProxy.beaconStack)) {
            return "bmu.block.beacon";
        }

        return CommonProxy.bmuBlock.getBlockName();
    }
}
