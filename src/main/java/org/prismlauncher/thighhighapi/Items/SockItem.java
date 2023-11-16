package org.prismlauncher.thighhighapi.Items;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.util.Identifier;

import static org.prismlauncher.thighhighapi.ThighHighs.resource;

public class SockItem extends TrinketItem {

    //initialize this for debugging purposes
    private Identifier textureName;
    public SockItem(Settings settings, String textureName) {
        super(settings);
        this.textureName = resource(textureName);
    }
    public SockItem(Settings settings, Identifier textureName) {
        super(settings);
        this.textureName = textureName;
    }

    //returns the texture name
    public Identifier getTextureName(){
        return this.textureName;
    }

    //for debugging purposes
    @Override
    public String toString() {
        return textureName.toString();
    }
}
