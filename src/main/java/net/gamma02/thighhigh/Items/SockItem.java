package net.gamma02.thighhigh.Items;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.item.Item;

public class SockItem extends TrinketItem {

    private final String textureName;
    public SockItem(Settings settings, String textureName) {
        super(settings);
        this.textureName = textureName;
    }

    public String getTextureName(){
        return this.textureName;
    }
}
