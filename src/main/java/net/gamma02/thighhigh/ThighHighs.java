package net.gamma02.thighhigh;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.gamma02.thighhigh.Compat.Trinkets;
import net.gamma02.thighhigh.Data.ClientResourceReloadListener;
import net.gamma02.thighhigh.Data.ServerResourceReloadListener;
import net.gamma02.thighhigh.Data.SockItemDesearlizer;
import net.gamma02.thighhigh.Items.SockItemType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.slf4j.Logger;
import net.gamma02.thighhigh.mixin.BeforeFreezeDataReloader;

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

    //this is the hash map we use to keep track of our registered socks!
    public static HashMap<Identifier, SockItemType> RegisteredSocks = new HashMap<>();


    //test item
    public static SockItemType TEST_SOCK = new SockItemType(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1), "socks", "test_socks");



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

        RegisteredSocks.put(resource("test_socks"), TEST_SOCK);

        register(TEST_SOCK, Registries.ITEM, resource("test_socks"));

        Trinkets.initTrinkets();

    }




    @Override
    public void onInitialize() {
        //Register socks defined in JSON and add them to the socks trinket tag.
        while (!SockItemsToRegister.isEmpty()) {
            var sock = SockItemsToRegister.pop();

            Identifier sockId = sock.name;

            LOGGER.info(sockId.toString());

            SockItemType registered = register(sock, Registries.ITEM, sockId);//SHOULD BE THIS

            RegisteredSocks.replace(sockId, registered);

            Trinkets.registerSockTrinket(registered);
        }


        Trinkets.registerSockTrinket(TEST_SOCK);

    }



    public static <A, T extends A> T register(T object, Registry<A> registry, Identifier id){
        return Registry.register(registry, id.toString(), object);
    }

}
