package net.gamma02.thighhigh.mixin;


import net.gamma02.thighhigh.ThighHighs;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.item.Item;
import net.minecraft.registry.*;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.SaveLoading;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.minecraft.server.SaveLoading.*;

@Mixin(SimpleRegistry.class)
public abstract class FreezeRegistryMixin<T> {

    private static boolean reloadAgain = true;

    @Shadow @Final private RegistryKey<? extends Registry<T>> key;

    @Shadow public abstract Registry<T> freeze();

    @Inject(method = "freeze", at = @At("HEAD"))
    public void freezeMixin(CallbackInfoReturnable<Registry<T>> cir){
//        if(cir.getReturnValue().em)
        if(Objects.equals(this.key, RegistryKeys.ITEM) && reloadAgain) {
            System.out.println("Reloading before");
            ResourcePackManager resourcePackManager = new ResourcePackManager(new VanillaDataPackProvider());
//        var dataConfiguration = DataConfiguration.SAFE_MODE.withFeaturesAdded(this.initialDataConfig.enabledFeatures()); - find a way to do this with all of them if this doesn't work!
            SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, DataConfiguration.SAFE_MODE.withFeaturesAdded(FeatureFlags.DEFAULT_ENABLED_FEATURES), false, true);

            var result = dataPacks.load();//whops gotta have that lmao

            ThighHighs.SEVER_DATA_RELOAD.reload(result.getSecond());//lets go this is so dumb
//            result.getSecond().streamResourcePacks();
            reloadAgain = false;

        }


    }
}
