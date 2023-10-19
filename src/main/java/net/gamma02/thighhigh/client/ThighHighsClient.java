package net.gamma02.thighhigh.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.gamma02.thighhigh.client.modelStuff.sock_layer;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static net.minecraft.client.render.entity.model.EntityModelLayers.LAYERS;

public class ThighHighsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(SOCKS_LAYER_NORMAL, () -> normal_socks_data);
        EntityModelLayerRegistry.registerModelLayer(SOCKS_LAYER_SLIM, () -> slim_socks_data);
    }

    public static TexturedModelData normal_socks_data = sock_layer.createBodyLayer(new Dilation(1.0f));
    public static TexturedModelData slim_socks_data = sock_layer.createBodyLayer(new Dilation(0.98f));



    public static EntityModelLayer SOCKS_LAYER_NORMAL = register("player", "socks_layer_normal");
    public static EntityModelLayer SOCKS_LAYER_SLIM = register("player", "socks_layer_slim");



    private static EntityModelLayer register(String id, String layer) {
        EntityModelLayer entityModelLayer = create(id, layer);
        if (!LAYERS.add(entityModelLayer)) {
            throw new IllegalStateException("Duplicate registration for " + entityModelLayer);
        } else {
            System.out.println("Registered " + id + ":" + layer);
            return entityModelLayer;
        }
    }
    private static EntityModelLayer create(String id, String layer) {
        return new EntityModelLayer(new Identifier("thighhigh", id), layer);
    }
}
