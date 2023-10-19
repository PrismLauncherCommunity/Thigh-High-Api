package net.gamma02.thighhigh.mixin;

import net.gamma02.thighhigh.client.ThighHighsClient;
import net.gamma02.thighhigh.client.modelStuff.ThighHighLayerRenderer;
import net.gamma02.thighhigh.client.modelStuff.sock_layer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {


    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void layerAdderMixin(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci){

        this.addFeature(new ThighHighLayerRenderer<>(this, new sock_layer<>(ctx.getPart(slim ? ThighHighsClient.SOCKS_LAYER_SLIM : ThighHighsClient.SOCKS_LAYER_NORMAL))));
    }

}
