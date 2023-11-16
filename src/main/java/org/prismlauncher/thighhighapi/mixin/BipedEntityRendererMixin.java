package org.prismlauncher.thighhighapi.mixin;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.prismlauncher.thighhighapi.client.ThighHighsClient;
import org.prismlauncher.thighhighapi.client.modelStuff.ThighHighLayerRenderer;
import org.prismlauncher.thighhighapi.client.modelStuff.sock_layer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityRenderer.class)
public abstract class BipedEntityRendererMixin<T extends LivingEntity, R extends BipedEntityModel<T>> extends LivingEntityRenderer<T, R> {


    public BipedEntityRendererMixin(EntityRendererFactory.Context ctx, R model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Lnet/minecraft/client/render/entity/model/BipedEntityModel;F)V", at = @At("RETURN"))
    public void layerAdderMixin(EntityRendererFactory.Context ctx, BipedEntityModel model, float shadowRadius, CallbackInfo ci){
        this.addFeature(new ThighHighLayerRenderer<>(this, new sock_layer<>(ctx.getPart(ThighHighsClient.SOCKS_LAYER_NORMAL))));
    }

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Lnet/minecraft/client/render/entity/model/BipedEntityModel;FFFF)V", at = @At("RETURN"))
    public void layerAdderMixin(EntityRendererFactory.Context ctx, BipedEntityModel model, float shadowRadius, float scaleX, float scaleY, float scaleZ, CallbackInfo ci){
        this.addFeature(new ThighHighLayerRenderer<>(this, new sock_layer<>(ctx.getPart(ThighHighsClient.SOCKS_LAYER_NORMAL))));
    }



}
