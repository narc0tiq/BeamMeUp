package bmu;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class CommonProxy {
    public static BlockBMU bmuBlock;
    public static BlockBeacon beaconBlock;

    public static ItemStack transporterStack;
    public static ItemStack interdictorStack;
    public static ItemStack beaconStack;

    public static int interdictorRange = 8;

    public static int lockDuration     = 600;
    public static int lockCooldown     =  20;
    public static int lockLossDuration =  20;
    public static int lockLossCooldown = 200;

    public static int transporterBaseSignal         = 500;
    public static double signalPerBlock             = 1.0D;
    public static double transporterBoostMultiplier = 0.1D;
    public static double beaconMultiplier           = 0.8D;
    public static double[] interdictorSignalRatios  = new double[] { 0.5D, 0.4D, 0.375D, 0.33D };
    public static double[] lockThresholds           = new double[] { 0.9D, 0.5D, 0.1D };
    public static double lockVariance               = 0.05D;

    public static int interdictorBaseConsumption        = 4;
    public static int[] interdictorDampeningConsumption = new int[] { 1, 2, 3, 4 };
    public static int transporterBoostEU                = 512;

    public static final String CHANNEL_NAME = "bmu";

    public static final byte PACKET_TELEPORT      = 0;
    public static final byte PACKET_MACHINE_STATE = 1;

    public void init() {
        initBlocks(BeamMeUp.config);
        initLanguage();
        initSettings(BeamMeUp.config);
        initEntity();
    }

    public void initBlocks(Configuration config) {
        Property blockID = config.getBlock("machine", 513);
        blockID.comment = "All BMU machines (i.e. transporter, interdictor) share this block ID.";
        bmuBlock = new BlockBMU(blockID.getInt());
        GameRegistry.registerBlock(bmuBlock, BlockBMUItem.class, bmuBlock.getBlockName());

        blockID = config.getBlock("beacon", 514);
        blockID.comment = "The beacon needs its own block ID.";
        beaconBlock = new BlockBeacon(blockID.getInt());
        GameRegistry.registerBlock(beaconBlock, ItemBlock.class, beaconBlock.getBlockName());

        transporterStack = new ItemStack(bmuBlock, 1, BlockBMU.DATA_TRANSPORTER);
        interdictorStack = new ItemStack(bmuBlock, 1, BlockBMU.DATA_INTERDICTOR);
        beaconStack      = new ItemStack(beaconBlock, 1);

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
        Property interdictorRange = config.get("general", "interdictor.range", this.interdictorRange);
        interdictorRange.comment = "How far from itself will an interdictor work? Default is 8 blocks, giving it a range of 17x17x17.";
        Property positionImprecision = config.get("general", "lock.imprecision", 16);
        positionImprecision.comment = "How far (on each axis) can a poor lock send us, at worst?";

        config.addCustomCategoryComment("time", "All times are expressed in ticks (1/20th of a second, usually).");
        Property lockDuration = config.get("time", "lock.duration", this.lockDuration);
        lockDuration.comment = "How long can a transporter lock be held? Default is 600 ticks (30 seconds) to go from 100% to 0%.";
        Property lockCooldown = config.get("time", "lock.cooldown", this.lockCooldown);
        lockCooldown.comment = "How long after releasing a lock until the transporter is ready to (attempt to) acquire another?";
        Property lockLossDuration = config.get("time", "lock.loss.duration", this.lockLossDuration);
        lockLossDuration.comment = "The longest a lock can be held below the worst threshold (see signal.lock.thresholds)";
        Property lockLossCooldown = config.get("time", "lock.loss.cooldown", this.lockLossCooldown);
        lockLossCooldown.comment = "After losing a lock to poor signal strength, how long do we have to wait to try again?";

        config.addCustomCategoryComment("signal", "Signal units are used internally, but tweaking them is an option.");
        Property transporterBaseSignal = config.get("signal", "transporter", this.transporterBaseSignal);
        transporterBaseSignal.comment = "The base signal strength for a transporter, without boosting or dampening.";
        Property signalPerBlock = config.get("signal", "per.block", this.signalPerBlock);
        signalPerBlock.comment = "The signal cost for each block of distance covered by a teleport.";
        Property transporterBoostMultiplier = config.get("signal", "transporter.max.boost", this.transporterBoostMultiplier);
        transporterBoostMultiplier.comment = "The maximum bonus signal a transporter can achieve using EU/t for a locking boost. Note the bonus is a fraction of the _required_ signal.";
        Property beaconMultiplier = config.get("signal", "beacon.ratio", this.beaconMultiplier);
        beaconMultiplier.comment = "Locking onto a beacon requires this ratio of the normal required signal.";
        Property interdictorSignalRatios = config.get("signal", "interdictor.ratios", this.interdictorSignalRatios);
        interdictorSignalRatios.comment = "The ratio of remaining signal after each interdictor applies its dampening. Note these are applied in sequence, i.e. two interdictors apply 0.5 * 0.4 to the original signal.";
        Property lockThresholds = config.get("signal", "lock.thresholds", this.lockThresholds);
        lockThresholds.comment = "The thresholds for telling apart perfect, good, bad, and terrible signal strengths.";
        Property lockVariance = config.get("signal", "lock.variance", this.lockVariance);
        lockVariance.comment = "Standard deviation for the bell curve (Gaussian) random variance that transport locks have. This is *NOT* the maximum variation possible!";

        Property interdictorBaseConsumption = config.get("eu", "interdictor", this.interdictorBaseConsumption);
        interdictorBaseConsumption.comment = "An interdictor consumes this much EU/t at all times when enabled.";
        Property interdictorDampeningConsumption = config.get("eu", "interdictor.dampening", this.interdictorDampeningConsumption);
        interdictorDampeningConsumption.comment = "Interdiction EU/t costs for 1, 2, 3, and 4 interdictors all applying to the same target lock.";
        Property transporterLockEU = config.get("eu", "transporter.lock", 512);
        transporterLockEU.comment = "Maintaining a lock costs this much EU/t.";
        Property transporterBadLockEU = config.get("eu", "transporter.max.lock", 1024);
        transporterBadLockEU.comment = "Additionally, it may cost up to this much extra EU/t to maintain a bad lock (see lock.thresholds).";
        Property transporterBoostEU = config.get("eu", "transporter.max.boost", this.transporterBoostEU);
        transporterBoostEU.comment = "A transporter can be ordered to use up to this much extra EU/t to boost a lock by signal.transporter.max.boost.";
        Property transportCostPerBlock = config.get("eu", "transport.distance", 100);
        transportCostPerBlock.comment = "Each block of distance transported across costs this much EU for the teleport.";
        Property transportCostPerItem = config.get("eu", "transport.item", 1000);
        transportCostPerItem.comment = "Each item (not stack!) in an inventory costs this much EU to teleport. Note that blocks are considered single items, as are entities.";

        this.interdictorRange = interdictorRange.getInt(this.interdictorRange);

        this.lockDuration = lockDuration.getInt(this.lockDuration);
        this.lockCooldown = lockCooldown.getInt(this.lockCooldown);
        this.lockLossDuration = lockLossDuration.getInt(this.lockLossDuration);
        this.lockLossCooldown = lockLossCooldown.getInt(this.lockLossCooldown);


        this.transporterBaseSignal = transporterBaseSignal.getInt(this.transporterBaseSignal);
        this.signalPerBlock = signalPerBlock.getDouble(this.signalPerBlock);
        this.transporterBoostMultiplier = transporterBoostMultiplier.getDouble(this.transporterBoostMultiplier);
        this.beaconMultiplier = beaconMultiplier.getDouble(this.beaconMultiplier);
        this.interdictorSignalRatios = interdictorSignalRatios.getDoubleList();
        this.lockThresholds = lockThresholds.getDoubleList();
        this.lockVariance = lockVariance.getDouble(this.lockVariance);

        this.interdictorBaseConsumption = interdictorBaseConsumption.getInt(this.interdictorBaseConsumption);
        this.interdictorDampeningConsumption = interdictorDampeningConsumption.getIntList();
        this.transporterBoostEU = transporterBoostEU.getInt(this.transporterBoostEU);
    }

    public void initEntity() {
        int eid = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerModEntity(EntityTransporterHelper.class, "bmu.transporterhelper", 
                eid, BeamMeUp.instance, 50, 1, true);
    }

    public World getClientWorld() {
        return null;
    }

    public String getPlayerName() {
        return "";
    }
}
