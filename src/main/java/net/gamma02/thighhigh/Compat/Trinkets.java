package net.gamma02.thighhigh.Compat;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.util.TriState;
import net.gamma02.thighhigh.Items.SockItem;

import static net.gamma02.thighhigh.ThighHighs.resource;

public class Trinkets {

    public static void initTrinkets(){
        TrinketsApi.registerTrinketPredicate(resource("is_sock"), (stacl, ref, entity) -> TriState.of(stacl.getItem() instanceof SockItem && ref.inventory().getSlotType().getName().contains("socks")));
    }



    public static void registerSockTrinket(SockItem sock){
        TrinketsApi.registerTrinket(sock, sock);
    }
}
