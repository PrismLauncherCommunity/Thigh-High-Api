package org.prismlauncher.thighhighapi.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.prismlauncher.thighhighapi.Data.ArbitraryDataReloadHelper;
import org.prismlauncher.thighhighapi.ThighHighs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.prismlauncher.thighhighapi.ThighHighs.MOD_LOGGER;

@Mixin(Items.class)
public class BeforeFreezeDataReloader {

    private static int counter = 0;
    @Inject(method = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("HEAD"))
    private static void reloadResourcePacksBeforeRegistryFreeze(String id, Item item, CallbackInfoReturnable<Item> cir){


        counter+=1;
        if(ThighHighs.reloadServerDataAgain && counter > 10) {
            MOD_LOGGER.debug("Reloading while registering!");
            ArbitraryDataReloadHelper.importCustomDatapacksAndLoad();

        }






    }




}
