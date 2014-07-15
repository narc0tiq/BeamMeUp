package bmu;

import net.minecraft.util.Icon;
import net.minecraft.client.renderer.texture.IconRegister;

public enum BlockIcons {
    TELEPORTER_SIDE("teleporter-side");

    public final String textureName;
    public final String iconRegistryName;

    BlockIcons(String textureName) {
        this.textureName = textureName;
        this.iconRegistryName = BeamMeUp.MODID.toLowerCase() + ":" + textureName;
    }

    public Icon register(IconRegister ir) {
        return ir.registerIcon(this.iconRegistryName);
    }
}
