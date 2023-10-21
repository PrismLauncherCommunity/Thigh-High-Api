package net.gamma02.thighhigh.mixin;


import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.gamma02.thighhigh.Items.SockItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @Shadow protected abstract void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model);

    @Unique
    private boolean isRenderingSocks = false;

    //this is MUCH better than what it was... These Injects disable armor rendering over our socks!
    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
    public void storeIsPlayerWearingSocks(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci){
        if(livingEntity instanceof PlayerEntity player && TrinketsApi.getTrinketComponent(player).isPresent()) {
            TrinketComponent component = TrinketsApi.getTrinketComponent(player).get();
            isRenderingSocks = component.isEquipped((stack) -> stack.getItem() instanceof SockItem);//Pretty sure there's a way to speed this up if it becomes a problem...
        }
    }

    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V", ordinal = 1))
    private void renderLegs(ArmorFeatureRenderer<T, M, M> instance, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model){
        checkAndRenderArmor(instance, matrices, vertexConsumers, entity, armorSlot, light, model);
    }
    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V", ordinal = 2))
    private void renderBoots(ArmorFeatureRenderer<T, M, M> instance, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model){
        checkAndRenderArmor(instance, matrices, vertexConsumers, entity, armorSlot, light, model);
    }

    @Unique
    private void checkAndRenderArmor(ArmorFeatureRenderer<T, M, M> instance, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model){
        if(!isRenderingSocks){
            renderArmor(matrices, vertexConsumers, entity, armorSlot, light, model);
        }
    }

    //let's get rid of this cursedness
//    @Overwrite
//    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
//        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.CHEST, i, this.getModel(EquipmentSlot.CHEST));
//        if(livingEntity instanceof PlayerEntity player && TrinketsApi.getTrinketComponent(player).isPresent()) {
//            TrinketComponent component = TrinketsApi.getTrinketComponent(player).get();
//            if(!component.isEquipped((stack) -> stack.getItem() instanceof SockItem)) {
//                this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.LEGS, i, this.getModel(EquipmentSlot.LEGS));
//                this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.FEET, i, this.getModel(EquipmentSlot.FEET));
//            }
//        }
//        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.HEAD, i, this.getModel(EquipmentSlot.HEAD));
//    }





}
