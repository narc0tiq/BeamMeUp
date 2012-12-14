package bmu;

import java.lang.reflect.Method;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.Entity;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;

public class EntityTransporterHelper extends Entity {
    public EntityTransporterHelper(World world) {
        super(world);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderVec3D(Vec3 vec) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double dist) {
        return false;
    }

    @Override
    public boolean shouldRiderSit() {
        if(riddenByEntity != null) {
            try {
                Method m = Entity.class.getDeclaredMethod("%conf:Entity.setFlag.obfuscated%", int.class, boolean.class);
                m.setAccessible(true);
                m.invoke(riddenByEntity, 2, false);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public double getMountedYOffset() {
        return 0.5D;
    }

    @Override protected void entityInit() {}
    @Override protected void readEntityFromNBT(NBTTagCompound tag) {}
    @Override protected void writeEntityToNBT(NBTTagCompound tag) {}

}

