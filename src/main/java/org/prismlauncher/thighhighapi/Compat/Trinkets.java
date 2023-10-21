package org.prismlauncher.thighhighapi.Compat;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.player.PlayerEntity;
import org.prismlauncher.thighhighapi.Items.SockItem;

import static org.prismlauncher.thighhighapi.ThighHighs.resource;

public class Trinkets {

    public static void initTrinkets(){
        TrinketsApi.registerTrinketPredicate(resource("is_sock"), (stacl, ref, entity) -> TriState.of(stacl.getItem() instanceof SockItem && ref.inventory().getSlotType().getName().contains("socks")));
    }

    public static boolean isPlayerWearingSocks(PlayerEntity player){
        if(TrinketsApi.getTrinketComponent(player).isPresent()) {
            TrinketComponent component = TrinketsApi.getTrinketComponent(player).get();
            return component.isEquipped((stack) -> stack.getItem() instanceof SockItem);//Pretty sure there's a way to speed this up if it becomes a problem...
        }
        return false;
    }



    public static void registerSockTrinket(SockItem sock){
        TrinketsApi.registerTrinket(sock, sock);
    }
}
