package org.prismlauncher.thighhighapi.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.entity.LivingEntity;
import org.prismlauncher.thighhighapi.Compat.Trinkets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnimalModel.class)
public class AnimalModelMixin {


    @Redirect(method = "render", at = @At(target = "Lnet/minecraft/client/render/entity/model/AnimalModel;getBodyParts()Ljava/lang/Iterable;", value = "INVOKE"))
    private <T extends LivingEntity> Iterable<ModelPart> disableLeggingRendering(AnimalModel<T> instance){
        if(Trinkets.isPlayerWearingSocks(MinecraftClient.getInstance().player)/*this is what i do when im not exposed an entity >:(*/
            && instance instanceof ArmorEntityModel<T> model){
            return ImmutableList.of(model.body, model.rightArm, model.leftArm, model.hat);
        }
        return instance.getBodyParts();
    }
}
