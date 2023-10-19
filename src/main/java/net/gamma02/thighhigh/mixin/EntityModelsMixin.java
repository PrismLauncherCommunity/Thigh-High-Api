package net.gamma02.thighhigh.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.gamma02.thighhigh.client.ThighHighsClient.*;

@Mixin(EntityModels.class)
public class EntityModelsMixin {

//    @Redirect(method = "getModels", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;", ordinal = 0))
//    private static <K, V> com.google.common.collect.ImmutableMap.Builder<K,V> putRedirect(ImmutableMap.Builder<K,V> instance, K key, V value){
//
//        System.out.println("Registering Allay, Socks!");
//
//        instance.put((K) normal_socks_data, (V) SOCKS_LAYER_NORMAL);
//        instance.put((K) slim_socks_data, (V) SOCKS_LAYER_SLIM);
//        instance.put(key, value);
//
//        return instance;
//    }
}
