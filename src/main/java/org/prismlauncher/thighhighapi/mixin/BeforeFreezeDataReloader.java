package org.prismlauncher.thighhighapi.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourcePackManager;
import org.prismlauncher.thighhighapi.Data.ArbitraryDataReloadHelper;
import org.prismlauncher.thighhighapi.ThighHighs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static org.prismlauncher.thighhighapi.ThighHighs.MOD_LOGGER;

@Mixin(Items.class)
public class BeforeFreezeDataReloader {

    private static int counter = 0;
    @Inject(method = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("HEAD"))
    private static void reloadResourcePacksBeforeRegistryFreeze(String id, Item item, CallbackInfoReturnable<Item> cir){


        counter+=1;
        if(ThighHighs.reloadServerDataAgain && counter > 10) {
            MOD_LOGGER.debug("Reloading while registering!");
            ResourcePackManager resourcePackManager = ArbitraryDataReloadHelper.getResourcePackManager();

            try {
                //make the manager find all of the existing ResourcePacks
                resourcePackManager.scanPacks();

                //grab a list of the resource pack names
                List<String> resourcePackNames = resourcePackManager.getNames().stream().toList();

                //build a NBT-string list of the resource pack names
                String names = ArbitraryDataReloadHelper.buildNbtString(resourcePackNames);
                //build a Dynamic-readable list of datapack names
                var nbtCompound2 = StringNbtReader.parse("{DataPacks:{Enabled:%s,Disabled:[]}}".formatted(names));


                //look at this cursedness - I'm using an Object to trick the comipler into thinking NbtOps.INSTANCE is a subclass of DynamicOps<NbtCompound>
                Object ops = NbtOps.INSTANCE;
                //that said, it IS an instance of DynamicOps<NbtCompound>, so there's literally no reason this should error. If it does, this is the reason and god has forsaken us.
                Dynamic<NbtCompound> dyn = new Dynamic<>((DynamicOps<NbtCompound>)ops, nbtCompound2);

                //This was stolen from Mojang code - don't blame for how weird it is. Stolen from SaveLoading#DataPacks, taken into ArbitraryDataReloader to take those methods out of ThighHighs
                ArbitraryDataReloadHelper.DataPacks dataPacks = new ArbitraryDataReloadHelper.DataPacks(resourcePackManager, ArbitraryDataReloadHelper.parseDataPackSettings(dyn), false, false);

                //yeah don't forget to actually load the datapacks...
                Pair<DataConfiguration, LifecycledResourceManager> result = dataPacks.load();//whops gotta have that lmao

                //Directly call the reload class on our
                ThighHighs.SEVER_DATA_RELOAD.reload(result.getSecond());//lets go this is so dumb
                ThighHighs.reloadServerDataAgain = false;

            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }




        }






    }




}
