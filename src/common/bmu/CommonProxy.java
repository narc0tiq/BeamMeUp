package bmu;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class CommonProxy {
    public static BlockBMU bmuBlock;

    public static ItemStack transporterStack;
    public static ItemStack interdictorStack;
    public static ItemStack beaconStack;

    public static int maxInterdictorRange;

    public void init() {
        initBlocks(BeamMeUp.config);
        initLanguage();
        initSettings(BeamMeUp.config);
    }

    public void initBlocks(Configuration config) {
        Property blockID = config.getBlock("bmu", 513);
        blockID.comment = "All BMU blocks share this block ID.";
        bmuBlock = new BlockBMU(blockID.getInt());
        GameRegistry.registerBlock(bmuBlock, BlockBMUItem.class);

        transporterStack = new ItemStack(bmuBlock, 1, BlockBMU.DATA_TRANSPORTER);
        interdictorStack = new ItemStack(bmuBlock, 1, BlockBMU.DATA_INTERDICTOR);
        beaconStack      = new ItemStack(bmuBlock, 1, BlockBMU.DATA_BEACON);

        GameRegistry.registerTileEntity(TileEntityTransporter.class, "bmu.transporter.entity");
        GameRegistry.registerTileEntity(TileEntityInterdictor.class, "bmu.interdictor.entity");
        GameRegistry.registerTileEntity(TileEntityBeacon.class,      "bmu.beacon.entity");
    }

    public void initLanguage() {
        LanguageRegistry.addName(bmuBlock, "Unknown BMU block");
        LanguageRegistry.addName(transporterStack, "Transporter");
        LanguageRegistry.addName(interdictorStack, "Interdictor");
        LanguageRegistry.addName(beaconStack, "Transport Beacon");
    }

    public void initSettings(Configuration config) {
        Property dictorRange = config.get(Configuration.CATEGORY_GENERAL, "interdictor.range", 8);
        dictorRange.comment = "How far from itself will an interdictor work? Defaults to 8 blocks, giving it a range of 17x17x17.";
        maxInterdictorRange = dictorRange.getInt();
    }
}
