package org.prismlauncher.thighhighapi.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Final public GameOptions options;

    @Shadow public abstract void openPauseMenu(boolean pause);

    @Inject(method = "<init>", at = @At("RETURN"))
    public void loadPack(RunArgs args, CallbackInfo ci){
        GameOptions options = MinecraftClient.getInstance().options;

        List<String> resourcePacks = options.resourcePacks;
        System.out.println();

        if(!resourcePacks.contains("file/thighhighapidefaultdata.zip")){
            resourcePacks.add("file/thighhighapidefaultdata.zip");
            options.resourcePacks = resourcePacks;
            MinecraftClient.getInstance().getResourcePackManager().enable("file/thighhighapidefaultdata.zip");
            MinecraftClient.getInstance().reloadResources();
            options.write();
        }
    }
}
