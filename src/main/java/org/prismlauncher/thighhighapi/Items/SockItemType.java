package org.prismlauncher.thighhighapi.Items;

import org.prismlauncher.thighhighapi.ThighHighs;
import net.minecraft.util.Identifier;

public class SockItemType extends SockItem {

    public Identifier name;

    /**
     * This is for JSON deserialization and registry of items
     * @param settings item settings
     * @param textureName name of texture
     * @param name registry name of item
     * @see org.prismlauncher.thighhighapi.Data.SockItemDesearlizer
     * @see org.prismlauncher.thighhighapi.Data.ServerResourceReloadListener
     */
    public SockItemType(Settings settings, String textureName, String name) {
        super(settings, textureName);
        this.name = ThighHighs.resource(name);
    }

    //for debugging purposes
    @Override
    public String toString() {
        return name.toString();
    }
}
