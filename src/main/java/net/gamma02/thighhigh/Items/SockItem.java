package net.gamma02.thighhigh.Items;

import dev.emi.trinkets.api.TrinketItem;

public class SockItem extends TrinketItem {

    //initialize this for debugging purposes
    private String textureName = "socks";
    public SockItem(Settings settings, String textureName) {
        super(settings);
        this.textureName = textureName;
    }

    //returns the texture name
    public String getTextureName(){
        return this.textureName;
    }

    //for debugging purposes
    @Override
    public String toString() {
        return textureName;
    }
}
