package org.prismlauncher.thighhighapi.Data;

import com.google.gson.*;
import org.prismlauncher.thighhighapi.Items.SockItemType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Locale;

public class SockItemDesearlizer implements JsonDeserializer<SockItemType> {

    public static SockItemDesearlizer INSTANCE = new SockItemDesearlizer();
    @Override
    public SockItemType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //get the JsonObject from the element and store it for more consise code
        JsonObject sockItem = json.getAsJsonObject();

        //get the name of the texture file
        String textureName = sockItem.get("texture_name").getAsString();

        //get the identifier path of the item - it's registry name
        String itemName = sockItem.get("name").getAsString();

        //See if there is an item properties JSON block
        if(sockItem.has("item_properties")) {
            int maxCount = 1;//don't usually stack armor but you do you ig
            int maxDamage = 1;
            @Nullable
            Item recipeRemainder = null;
            Rarity rarity = Rarity.COMMON;
            boolean fireproof = false;
//            FeatureSet requiredFeatures;

            JsonObject itemProperties = sockItem.getAsJsonObject("item_properties");

            //Ensure each property exists and import them
            if(itemProperties.has("max_count")){
                maxCount = itemProperties.get("max_count").getAsInt();
            }

            if(itemProperties.has("max_damage") && maxCount == 1){
                maxDamage = itemProperties.get("max_damage").getAsInt();
            }

            if(itemProperties.has("recipe_remainder")){
                recipeRemainder = Registries.ITEM.get(new Identifier(itemProperties.get("recipe_remainder").getAsString()));
            }

            if(itemProperties.has("rarity")){
                rarity = getRarity(itemProperties.get("rarity").getAsString());
            }

            if(itemProperties.has("fireproof")){
                fireproof = itemProperties.get("fireproof").getAsBoolean();
            }

            //build settings from the json
            Item.Settings itemSettings = new Item.Settings().rarity(rarity).maxCount(maxCount).maxDamage(maxDamage).recipeRemainder(recipeRemainder);
            //set fireproof, there's no better way to do this lol
            if(fireproof){
                itemSettings.fireproof();
            }

            //create new sock item with settings
            return new SockItemType(itemSettings, textureName, itemName);
        }

        // create new sock item with max count set to 1
        return new SockItemType(new Item.Settings().maxCount(1), textureName, itemName);
    }

    //turn a string that's either common, uncommon, rare, or epic into a rarity
    public Rarity getRarity(String rarity){
        return switch (rarity.toLowerCase(Locale.ROOT)) {
            case "common" -> Rarity.COMMON;
            case "uncommon" -> Rarity.UNCOMMON;
            case "rare" -> Rarity.RARE;
            case "epic" -> Rarity.EPIC;
            default -> Rarity.COMMON;
        };
    }
}
