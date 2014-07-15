package bmu;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;

public class BlockInfo {
    public static final String MACHINE_DEFAULT_KEY = "bmu:machine";

    public static Block machineBlock;

    public static void registerBlocks() {
        machineBlock = new MachineBlock(3591);

        GameRegistry.registerBlock(machineBlock, ItemBlockWithMetadata.class, MACHINE_DEFAULT_KEY);
        LanguageRegistry.addName(machineBlock, "Generic Machine Block");
    }
}
