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
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }


//    /**
//     * This function makes it so that the legs on the armor model "do not" render.
//     *
//     * @param instance of ModelPartData
//     * @param name name of child cube
//     * @param builder model part builder of original child cube
//     * @param rotationData rotation data of original child cube
//     * @return Updated instance of ModelPartData
//     */
//    @Redirect(method = "getModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPartData;addChild(Ljava/lang/String;Lnet/minecraft/client/model/ModelPartBuilder;Lnet/minecraft/client/model/ModelTransform;)Lnet/minecraft/client/model/ModelPartData;"))
//    private static ModelPartData redirectAddChild(ModelPartData instance, String name, ModelPartBuilder builder, ModelTransform rotationData){
//
//        instance.addChild(name, ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 0f, 0f, 0f, new Dilation(1f)), rotationData);
//
//        return instance;
//    }

    @Shadow protected abstract void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model);

    @Shadow protected abstract A getModel(EquipmentSlot slot);

    /**
     * @author Gamma_02
     * @reason remove armor layers when rendering
     */
    @Overwrite
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.CHEST, i, this.getModel(EquipmentSlot.CHEST));
        if(livingEntity instanceof PlayerEntity player && TrinketsApi.getTrinketComponent(player).isPresent()) {
            TrinketComponent component = TrinketsApi.getTrinketComponent(player).get();
            if(!component.isEquipped((stack) -> stack.getItem() instanceof SockItem)) {
                this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.LEGS, i, this.getModel(EquipmentSlot.LEGS));
                this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.FEET, i, this.getModel(EquipmentSlot.FEET));
            }
        }
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.HEAD, i, this.getModel(EquipmentSlot.HEAD));
    }





}
