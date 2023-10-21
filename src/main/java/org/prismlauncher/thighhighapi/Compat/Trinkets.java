package org.prismlauncher.thighhighapi.Compat;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.util.TriState;
import org.prismlauncher.thighhighapi.Items.SockItem;

import static org.prismlauncher.thighhighapi.ThighHighs.resource;

public class Trinkets {

    public static void initTrinkets(){
        TrinketsApi.registerTrinketPredicate(resource("is_sock"), (stacl, ref, entity) -> TriState.of(stacl.getItem() instanceof SockItem && ref.inventory().getSlotType().getName().contains("socks")));
    }



    public static void registerSockTrinket(SockItem sock){
        TrinketsApi.registerTrinket(sock, sock);
    }
}
