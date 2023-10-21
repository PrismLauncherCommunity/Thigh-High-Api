package org.prismlauncher.thighhighapi.mixin;


import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.trim.ArmorTrim;
import org.jetbrains.annotations.Nullable;
import org.prismlauncher.thighhighapi.client.ThighHighsClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }



    @Shadow protected abstract void setVisible(A bipedModel, EquipmentSlot slot);

    @Shadow protected abstract boolean usesInnerModel(EquipmentSlot slot);

    @Shadow protected abstract void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, A model, boolean secondTextureLayer, float red, float green, float blue, @Nullable String overlay);

    @Shadow protected abstract void renderTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings);

    @Shadow protected abstract void renderGlint(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model);


    //this is also unnecessary with the new system
    //this is MUCH better than what it was... These Injects disable armor rendering over our socks!
//    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
//    public void storeIsPlayerWearingSocks(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci){
////        if(livingEntity instanceof PlayerEntity player) {
////            //Pretty sure there's a way to speed this up if it becomes a problem...
////            isRenderingSocks = Trinkets.isPlayerWearingSocks(player);
////        }
//        ThighHighsClient.isRenderingPlayer = livingEntity.isPlayer();
//    }

    //
    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V", ordinal = 1))
    private void renderLegs(ArmorFeatureRenderer<T, M, M> instance, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model){
        renderArmorLegs(matrices, vertexConsumers, entity, armorSlot, light, model);
    }
//    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V", ordinal = 2))
//    private void renderBoots(ArmorFeatureRenderer<T, M, M> instance, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model){
//        checkAndRenderArmor(instance, matrices, vertexConsumers, entity, armorSlot, light, model);
//    }

    //mojang code, don't blame me for the horridness of it lmao
    @Unique
    private void renderArmorLegs(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model) {
        //set the entity in ThighHighsClient
        ThighHighsClient.currentlyRenderingEntity = entity;
        ItemStack itemStack = entity.getEquippedStack(armorSlot);
        Item item = itemStack.getItem();
        if (!(item instanceof ArmorItem armorItem)) {
            return;
        }
        if (armorItem.getSlotType() != armorSlot) {
            return;
        }
        (this.getContextModel()).copyBipedStateTo(model);
        this.setVisible(model, armorSlot);
        boolean usesInnerModel = this.usesInnerModel(armorSlot);
        //set if the model layer is leggings
        ThighHighsClient.isRenderingInnerArmor = usesInnerModel;
        if (armorItem instanceof DyeableArmorItem dyeableArmorItem) {
            int i = dyeableArmorItem.getColor(itemStack);
            float f = (float)(i >> 16 & 0xFF) / 255.0f;
            float g = (float)(i >> 8 & 0xFF) / 255.0f;
            float h = (float)(i & 0xFF) / 255.0f;
            this.renderArmorParts(matrices, vertexConsumers, light, armorItem, model, usesInnerModel, f, g, h, null);
            this.renderArmorParts(matrices, vertexConsumers, light, armorItem, model, usesInnerModel, 1.0f, 1.0f, 1.0f, "overlay");
        } else {
            this.renderArmorParts(matrices, vertexConsumers, light, armorItem, model, usesInnerModel, 1.0f, 1.0f, 1.0f, null);
        }
        //I honestly don't know wny my code works with trims but it does
        ArmorTrim.getTrim((entity).getWorld().getRegistryManager(), itemStack).ifPresent(trim -> this.renderTrim(armorItem.getMaterial(), matrices, vertexConsumers, light, trim, model, usesInnerModel));
        if (itemStack.hasGlint()) {
            this.renderGlint(matrices, vertexConsumers, light, model);
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
