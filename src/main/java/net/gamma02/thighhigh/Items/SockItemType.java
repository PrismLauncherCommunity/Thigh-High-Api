package net.gamma02.thighhigh.Items;

import com.google.gson.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.gamma02.thighhigh.ThighHighs;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Locale;

public class SockItemType extends SockItem {

//    public static SockItemType desearlizerInstance = new SockItemType(new Settings(), "deserializer", "deserializer");

    public Identifier name;
    public SockItemType(Settings settings, String textureName, String name) {
        super(settings, textureName);
        this.name = ThighHighs.resource(name);
    }

    public SockItemType(SockItem item, Settings settings, Identifier name){
        super(settings, item.getTextureName());
        this.name = name;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
