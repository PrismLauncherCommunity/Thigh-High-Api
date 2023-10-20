package net.gamma02.thighhigh.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.gamma02.thighhigh.ThighHighs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.SaveLoading;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.emi.trinkets.TrinketsMain.LOGGER;
import static net.minecraft.nbt.NbtOps.INSTANCE;

@Mixin(Items.class)
public class ItemsMixin {

    private static int counter = 0;
    @Inject(method = "register(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("HEAD"))
    private static void thingy(RegistryKey<Item> key, Item item, CallbackInfoReturnable<Item> cir){
        counter+=1;
        if(ThighHighs.reloadAgain && counter > 100) {
            System.out.println("Reloading before");
            ResourcePackManager resourcePackManager = new ResourcePackManager(new ModResourcePackCreator(ResourceType.SERVER_DATA), new VanillaDataPackProvider());
//        var dataConfiguration = DataConfiguration.SAFE_MODE.withFeaturesAdded(this.initialDataConfig.enabledFeatures()); - find a way to do this with all of them if this doesn't work!

            try {
                var nbtCompound2 = StringNbtReader.parse("{DataPacks:{Enabled:[vanilla,fabric],Disabled:[bundle]}}");

                Object ops = INSTANCE;
                Dynamic<NbtCompound> dyn = new Dynamic<NbtCompound>((DynamicOps<NbtCompound>)ops, nbtCompound2);
                SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, parseDataPackSettings(dyn), false, true);

                var result = dataPacks.load();//whops gotta have that lmao

                ThighHighs.SEVER_DATA_RELOAD.reload(result.getSecond());//lets go this is so dumb
//            result.getSecond().streamResourcePacks();
                ThighHighs.reloadAgain = false;

            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }

        }
    }
    private static DataConfiguration parseDataPackSettings(Dynamic<?> dynamic) {
        return DataConfiguration.CODEC.parse(dynamic).resultOrPartial(LOGGER/*i stole your logger >:D*/::error).orElse(DataConfiguration.SAFE_MODE);
    }
}
