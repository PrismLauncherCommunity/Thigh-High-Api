package net.gamma02.thighhigh.Items;

import com.google.gson.*;
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

public class SockItemType extends SockItem implements JsonDeserializer<SockItem> {

    public Identifier name;
    public SockItemType(Settings settings, String textureName, String name) {
        super(settings, textureName);
        this.name = ThighHighs.resource(name);
    }

    //This desearalizes item types!
    @Override
    public SockItemType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject sockItem = json.getAsJsonObject();

        String textureName = sockItem.get("texture_name").getAsString();

        String itemName = sockItem.get("item_registry_name").getAsString();

        if(sockItem.has("item_properties")) {
            int maxCount = 1;//don't usually stack tools
            int maxDamage = 1;
            @Nullable
            Item recipeRemainder = null;
            Rarity rarity = Rarity.COMMON;
            boolean fireproof = false;
//            FeatureSet requiredFeatures;

            JsonObject itemProperties = sockItem.getAsJsonObject("item_properties");

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
            Settings itemSettings = new Settings().rarity(rarity).maxCount(maxCount).maxDamage(maxDamage).recipeRemainder(recipeRemainder);
            if(fireproof){
                itemSettings.fireproof();
            }
            return new SockItemType(itemSettings, textureName, itemName);


        }




        return new SockItemType(new Settings(), textureName, itemName);
    }

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
