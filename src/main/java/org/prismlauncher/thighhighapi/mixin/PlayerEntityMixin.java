package org.prismlauncher.thighhighapi.mixin;

import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.prismlauncher.thighhighapi.Compat.Trinkets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    //This mixin disables skin layer 2 rendering while socks are being worn
    @Inject(method = "isPartVisible", at = @At("HEAD"), cancellable = true)
    public void preventLayerConflictsMixin(PlayerModelPart modelPart, CallbackInfoReturnable<Boolean> cir){
        if(this.getWorld().getEntityById(this.getId())/*...more mixin trickery*/ instanceof PlayerEntity player
                && Trinkets.isPlayerWearingSocks(player)
                && (
                        modelPart.getName().contains("left_pants")
                      || modelPart.getName().contains("right_pants")
                )){
            cir.setReturnValue(false);
        }
    }
}
