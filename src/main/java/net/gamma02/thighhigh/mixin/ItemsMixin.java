package net.gamma02.thighhigh.mixin;

import net.gamma02.thighhigh.ThighHighs;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.SaveLoading;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Items.class)
public class ItemsMixin {


    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void thingy(CallbackInfo ci){
        if(ThighHighs.reloadAgain) {
            System.out.println("Reloading before");
            ResourcePackManager resourcePackManager = new ResourcePackManager(new VanillaDataPackProvider());
//        var dataConfiguration = DataConfiguration.SAFE_MODE.withFeaturesAdded(this.initialDataConfig.enabledFeatures()); - find a way to do this with all of them if this doesn't work!
            SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, DataConfiguration.SAFE_MODE.withFeaturesAdded(FeatureFlags.DEFAULT_ENABLED_FEATURES), false, true);

            var result = dataPacks.load();//whops gotta have that lmao

            ThighHighs.SEVER_DATA_RELOAD.reload(result.getSecond());//lets go this is so dumb
//            result.getSecond().streamResourcePacks();
            ThighHighs.reloadAgain = false;

        }
    }
}
