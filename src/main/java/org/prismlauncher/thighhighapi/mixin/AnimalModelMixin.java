package org.prismlauncher.thighhighapi.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.prismlauncher.thighhighapi.Compat.Trinkets;
import org.prismlauncher.thighhighapi.client.ThighHighsClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnimalModel.class)
public abstract class AnimalModelMixin {


    @Shadow protected abstract Iterable<ModelPart> getBodyParts();

    @Redirect(method = "render", at = @At(target = "Lnet/minecraft/client/render/entity/model/AnimalModel;getBodyParts()Ljava/lang/Iterable;", value = "INVOKE"))
    private <T extends LivingEntity> Iterable<ModelPart> disableLeggingRendering(AnimalModel<T> instance){

        //check if a player is currently being rendered and the instance is an ArmorEntityModel and that the right armor model is being rendered
        if(ThighHighsClient.isRenderingInnerArmor
                && ThighHighsClient.currentlyRenderingEntity instanceof PlayerEntity player
                && instance instanceof ArmorEntityModel<T> model
                && Trinkets.isPlayerWearingSocks(player)){
            return ImmutableList.of(model.body, model.rightArm, model.leftArm, model.hat);
        }
        return this.getBodyParts();
    }
}
