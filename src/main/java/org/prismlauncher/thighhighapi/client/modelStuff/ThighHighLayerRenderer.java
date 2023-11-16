package org.prismlauncher.thighhighapi.client.modelStuff;

import com.google.common.collect.Maps;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.joml.Vector3f;
import org.prismlauncher.thighhighapi.Items.SockItem;
import org.prismlauncher.thighhighapi.ThighHighs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThighHighLayerRenderer <T extends LivingEntity, M extends BipedEntityModel<T>, A extends sock_layer<T>> extends FeatureRenderer<T, M> {

    //cache of loaded sock textures
    private static final Map<String, Identifier> SOCK_TEXTURE_CACHE = Maps.newHashMap();

    //model for us to render
    private final A sockModel;

    private Vector3f scaleVec;





    public ThighHighLayerRenderer(FeatureRendererContext<T, M> context, A sockModel) {
        super(context);
        this.sockModel = sockModel;
        this.scaleVec = new Vector3f(1.0f, 1.0f, 1.0f);
    }

    public ThighHighLayerRenderer(FeatureRendererContext<T, M> context, A sockModel, float scaleX, float scaleY, float scaleZ) {
        super(context);
        this.sockModel = sockModel;
        this.scaleVec = new Vector3f(scaleX, scaleY, scaleZ);
    }

    //do rendering of our socks if the player is wearing socks!
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
//        sockModel.render(matrices, vertexConsumers);
        if(entity != null && TrinketsApi.getTrinketComponent(entity).isPresent()) {
            TrinketComponent component = TrinketsApi.getTrinketComponent(entity).get();
            if(component.isEquipped((stack) -> stack.getItem() instanceof SockItem)) {
                List<ItemStack> sockStacks = component.getEquipped((stack) -> stack.getItem() instanceof SockItem).stream().map((Pair::getRight)).collect(Collectors.toList());
                for (ItemStack sockStack: sockStacks) {
                    (this.getContextModel()).copyBipedStateTo(sockModel);
                    matrices.scale(scaleVec.x(), scaleVec.y(), scaleVec.z());
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(this.getSockTexture((SockItem) /*this may cause issues*/sockStack.getItem())));
                    sockModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
                }
            }
        }
    }


    //based off of ArmorLayerRender#getArmorTexture, gets the texture of the socks
    private Identifier getSockTexture(SockItem item) {
        Identifier textureName = item.getTextureName();
        String textureLocation = textureName.getNamespace() + ":textures/models/socks/" + textureName.getPath() + ".png";
        return SOCK_TEXTURE_CACHE.computeIfAbsent(textureLocation, (string) -> textureLocation.contains(":") ? new Identifier(string) : ThighHighs.resource(string));
    }
}
