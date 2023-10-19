package net.gamma02.thighhigh;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.gamma02.thighhigh.Items.SockItem;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ThighHighs implements ModInitializer {

    public static String modid = "thighhigh";

    public static String MODID = modid;


    TagKey<Item> socks = TagKey.of(RegistryKeys.ITEM, new Identifier("trinkets", "items/legs/socks"));

    public static Identifier resource(String path){
        return new Identifier(modid, path);
    }



    public static SockItem SOCKS = new SockItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON), "socks");




    public ThighHighs(){

    }




    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, resource("socks"), SOCKS);

        TrinketsApi.registerTrinket(SOCKS, SOCKS);
    }
}
