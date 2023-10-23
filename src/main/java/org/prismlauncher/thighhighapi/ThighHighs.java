package org.prismlauncher.thighhighapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.prismlauncher.thighhighapi.Compat.Trinkets;
import org.prismlauncher.thighhighapi.Data.ClientResourceReloadListener;
import org.prismlauncher.thighhighapi.Data.ServerResourceReloadListener;
import org.prismlauncher.thighhighapi.Data.SockItemDesearlizer;
import org.prismlauncher.thighhighapi.Items.SockItemType;
import org.prismlauncher.thighhighapi.mixin.BeforeFreezeDataReloader;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.HashMap;

public class ThighHighs implements ModInitializer {



    public static String modid = "thighhigh";

    public static Logger MOD_LOGGER = LogUtils.getLogger();
    public static Logger LOGGER = LogUtils.getLogger();


    /**
     * This keeps track of weather or not we've reloaded the server data, we can't do that twice!
     * @see BeforeFreezeDataReloader
     */
    public static boolean reloadServerDataAgain = true;

    //generate one of our mod's identifiers
    public static Identifier resource(String path){
        return new Identifier(modid, path);
    }

    /**
     * This keeps track of the {@link SockItemType}s that we need to register.
     * @see BeforeFreezeDataReloader
     * @see ServerResourceReloadListener
     */
    public static ArrayDeque<SockItemType> SockItemsToRegister = new ArrayDeque<>();

    public static ItemGroup sockGroup = FabricItemGroup.builder().displayName(Text.translatable("thighhigh.sockgroup"))
            .icon(() -> Registries.ITEM.get(resource("test_socks")).getDefaultStack())
            .entries((displayContext, entries) -> {
                for(SockItemType type : ThighHighs.RegisteredSocks.values()){
                    entries.add(type.getDefaultStack());
                }
            }).build();

    //this is the hash map we use to keep track of our registered socks!
    public static HashMap<Identifier, SockItemType> RegisteredSocks = new HashMap<>();

    public static boolean loadResourcePack = false;


    //test item
//    public static SockItemType TEST_SOCK = new SockItemType(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1), "socks", "test_socks");



    //this is the Gson we use to deserialize items
    public static Gson ItemCreatingGSON = new GsonBuilder().registerTypeAdapter(SockItemType.class, SockItemDesearlizer.INSTANCE).create();

    /**
     * This is our datapack reload listener.
     * @see BeforeFreezeDataReloader
      */
    public static SimpleSynchronousResourceReloadListener SEVER_DATA_RELOAD = new ServerResourceReloadListener();

    @SuppressWarnings("unused")//we might need it someday
    public static SimpleSynchronousResourceReloadListener CLIENT_RESOURCE_RELOAD = new ClientResourceReloadListener();



    public ThighHighs(){

//        RegisteredSocks.put(resource("test_socks"), TEST_SOCK);
//
//        register(TEST_SOCK, Registries.ITEM, resource("test_socks"));

        Trinkets.initTrinkets();


    }




    @Override
    public void onInitialize() {

        Registry.register(Registries.ITEM_GROUP, resource("sock_group"), sockGroup);

        //Register socks defined in JSON and add them to the socks trinket tag.
//        while (!SockItemsToRegister.isEmpty()) {
//            var sock = SockItemsToRegister.pop();
//
//            Identifier sockId = sock.name;
//
//            LOGGER.info(sockId.toString());
//
//            SockItemType registered = register(sock, Registries.ITEM, sockId);//SHOULD BE THIS
//
//            RegisteredSocks.replace(sockId, registered);
//
//            Trinkets.registerSockTrinket(registered);
//        }


//        Trinkets.registerSockTrinket(TEST_SOCK);

    }



    public static <A, T extends A> T register(T object, Registry<A> registry, Identifier id){
        return Registry.register(registry, id.toString(), object);
    }

}
