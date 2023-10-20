package net.gamma02.thighhigh.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.loader.api.FabricLoader;
import net.gamma02.thighhigh.ThighHighs;
import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.client.resource.ServerResourcePackProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static dev.emi.trinkets.TrinketsMain.LOGGER;
import static net.gamma02.thighhigh.ThighHighs.MOD_LOGGER;
import static net.minecraft.nbt.NbtOps.INSTANCE;

@Mixin(Items.class)
public class ItemsMixin {

    private static int counter = 0;
    @Inject(method = "register(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("HEAD"))
    private static void thingy(RegistryKey<Item> key, Item item, CallbackInfoReturnable<Item> cir){


        counter+=1;
        if(ThighHighs.reloadServerDataAgain && counter > 10) {
            MOD_LOGGER.debug("Reloading before");
            var resourceType = ResourceType.SERVER_DATA;
            //load our data packs
            Path socksDataDir = Path.of("./resourcepacks");
            FileResourcePackProvider socksDataProvider = new FileResourcePackProvider(socksDataDir, resourceType, ResourcePackSource.SERVER);

            //load other mod's data packs
            var modDataPackCreator = new ModResourcePackCreator(resourceType);

            //add data packs to our data pack manager
            ResourcePackManager resourcePackManager = new ResourcePackManager(modDataPackCreator, socksDataProvider);
//        var dataConfiguration = DataConfiguration.SAFE_MODE.withFeaturesAdded(this.initialDataConfig.enabledFeatures()); - find a way to do this with all of them if this doesn't work!

            try {
                resourcePackManager.scanPacks();

//                String names = Arrays.toString(resourcePackManager.getNames().toArray());
                List<String> resourcePackNames = resourcePackManager.getNames().stream().toList();
                StringBuilder names = new StringBuilder("[");

                for(String packName : resourcePackNames){
                    if(!names.toString().equals("[")){
                        names.append(",");
                    }
                    names.append("\"").append(packName).append("\"");
                }
                names.append("]");
                var nbtCompound2 = StringNbtReader.parse("{DataPacks:{Enabled:%s,Disabled:[]}}".formatted(names.toString()));

                Object ops = INSTANCE;
                Dynamic<NbtCompound> dyn = new Dynamic<NbtCompound>((DynamicOps<NbtCompound>)ops, nbtCompound2);
                ThighHighs.DataPacks dataPacks = new ThighHighs.DataPacks(resourcePackManager, ThighHighs.parseDataPackSettings(dyn), false, false);

                var result = dataPacks.load();//whops gotta have that lmao

                ThighHighs.SEVER_DATA_RELOAD.reload(result.getSecond());//lets go this is so dumb
//            result.getSecond().streamResourcePacks();
                ThighHighs.reloadServerDataAgain = false;

            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }




        }






    }



}
