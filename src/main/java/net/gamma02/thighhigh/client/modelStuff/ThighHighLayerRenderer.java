package net.gamma02.thighhigh.client.modelStuff;

import com.google.common.collect.Maps;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.gamma02.thighhigh.Items.SockItem;
import net.gamma02.thighhigh.ThighHighs;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThighHighLayerRenderer <T extends LivingEntity, M extends BipedEntityModel<T>, A extends sock_layer<T>> extends FeatureRenderer<T, M> {

    private static final Map<String, Identifier> SOCK_TEXTURE_CACHE = Maps.newHashMap();



    private final A sockModel;





    public ThighHighLayerRenderer(FeatureRendererContext<T, M> context, A sockModel) {
        super(context);
        this.sockModel = sockModel;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
//        sockModel.render(matrices, vertexConsumers);
        if(entity instanceof PlayerEntity player && TrinketsApi.getTrinketComponent(player).isPresent()) {
            TrinketComponent component = TrinketsApi.getTrinketComponent(player).get();
            if(component.isEquipped((stack) -> stack.getItem() instanceof SockItem)) {
                List<ItemStack> sockStacks = component.getEquipped((stack) -> stack.getItem() instanceof SockItem).stream().map((Pair::getRight)).collect(Collectors.toList());
                for (ItemStack sockStack:
                     sockStacks) {
                    (this.getContextModel()).copyBipedStateTo(sockModel);
//                    sockModel.copySockStateFrom(this.getContextModel());

//                    this.getContextModel().copyBipedStateTo(this.sockModel);
//                    sockModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(this.getSockTexture((SockItem) /*this may cause issues*/sockStack.getItem())));
                    sockModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
                }
            }
        }
    }


    private Identifier getSockTexture(SockItem item) {
        String textureName = item.getTextureName();
        String textureLocation = "textures/models/socks/" + textureName + ".png";
        return SOCK_TEXTURE_CACHE.computeIfAbsent(textureLocation, ThighHighs::resource);
    }
}
