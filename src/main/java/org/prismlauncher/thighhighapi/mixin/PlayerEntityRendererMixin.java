package org.prismlauncher.thighhighapi.mixin;

import org.prismlauncher.thighhighapi.client.ThighHighsClient;
import org.prismlauncher.thighhighapi.client.modelStuff.ThighHighLayerRenderer;
import org.prismlauncher.thighhighapi.client.modelStuff.sock_layer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    //required constructor
    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    //This registers our custom entity layer renderer into the player class
    @Inject(method = "<init>", at = @At("RETURN"))
    public void layerAdderMixin(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci){
        this.addFeature(new ThighHighLayerRenderer<>(this, new sock_layer<>(ctx.getPart(slim ? ThighHighsClient.SOCKS_LAYER_SLIM : ThighHighsClient.SOCKS_LAYER_NORMAL))));
    }

}
