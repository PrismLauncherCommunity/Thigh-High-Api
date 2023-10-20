package net.gamma02.thighhigh;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.gamma02.thighhigh.Items.SockItem;
import net.gamma02.thighhigh.Items.SockItemDesearlizer;
import net.gamma02.thighhigh.Items.SockItemType;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ThighHighs implements ModInitializer {



    public static String modid = "thighhigh";

    public static String MODID = modid;




    public static TagKey<Item> socks = TagKey.of(RegistryKeys.ITEM, new Identifier("trinkets", "items/legs/socks"));

    public static Identifier resource(String path){
        return new Identifier(modid, path);
    }

    public static ArrayList<SockItemType> SockItemsToRegister = new ArrayList<>();

    public static ArrayList<RegistryEntry.Reference<Item>> socksToAddToTag = new ArrayList<>();

    public static HashMap<Identifier, SockItemType> RegisteredSocks = new HashMap<>();

    public static String socksDirectory = "data/thighhighs/socks";




    public static SockItem TEST_SOCK = new SockItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1), "socks");

    public static Gson gson = new GsonBuilder().registerTypeAdapter(SockItemType.class, SockItemDesearlizer.INSTANCE).create();

    public static SimpleSynchronousResourceReloadListener SEVER_DATA_RELOAD = new SimpleSynchronousResourceReloadListener() {
        @Override
        public Identifier getFabricId() {
            return resource("server_data");
        }

        @Override
        public void reload(ResourceManager manager) {
            System.out.println("Reloading on server!");

            try{
                Registries.ITEM.createEntry(TEST_SOCK);
            }catch (IllegalStateException e){
                System.out.println("REGISTRY FROZEN!");
                return;
            }


            for(Identifier id : manager.findResources(socksDirectory, (id) -> id.getPath().endsWith(".json")).keySet()) {
                try(InputStream stream = manager.getResource(id).get().getInputStream()) {
                    // Consume the stream however you want, medium, rare, or well done.
                    var item = gson.fromJson(new InputStreamReader(stream), SockItemType.class);

                    System.out.println("Registering socks: " + item.name);

                    SockItemsToRegister.add(item);
                } catch(Exception e) {
                    System.out.println("Error occurred while loading resource json " + id.toString() + e.getMessage());
                }

            }
        }
    };



    public ThighHighs(){
        registerAddTags(TEST_SOCK, Registries.ITEM, resource("test_socks"), socks);




        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SEVER_DATA_RELOAD);



        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                System.out.println("doing things!");
                return resource("client_data");
            }

            @Override
            public void reload(ResourceManager manager) {
                System.out.println("Reloadig on client!");

                try{
                    Registries.ITEM.createEntry(TEST_SOCK);
                }catch (IllegalStateException e){
                    System.out.println("REGISTRY FROZEN!");
                    return;
                }




            }
        });

    }




    @Override
    public void onInitialize() {
//        System.out.println("Doing stuff!");

        //socks directory:

        if(!SockItemsToRegister.isEmpty()) {


//        ArrayList<Identifier> socksToAddToTag = new ArrayList<>();

            //Register socks defined in JSON and add them to the socks trinket tag.
            for (SockItemType sock :
                    SockItemsToRegister) {

                Identifier sockId = sock.name;
                System.out.println(sockId);

                SockItemType registered = registerAddTags(sock, Registries.ITEM, sockId, socks);;//SHOULD BE THIS
                SockItemsToRegister.remove(sock);
                RegisteredSocks.put(sockId, registered);
                TrinketsApi.registerTrinket(registered, registered);
//            socksToAddToTag.add(sockId);
            }
        }


        TrinketsApi.registerTrinket(TEST_SOCK, TEST_SOCK);
    }



    @SafeVarargs
    public static <A, T extends A> T registerAddTags(T object, Registry<A> registry, Identifier id, TagKey<A>... tags){
        var registryEntry = Registry.registerReference(registry, id, object);
//        ArrayList<TagKey<A>> oldTags = new ArrayList<>(registryEntry.streamTags().toList());
//        oldTags.addAll(Arrays.stream(tags).toList());
//        registryEntry.setTags(oldTags);
        if(object instanceof SockItem i)
            socksToAddToTag.add((RegistryEntry.Reference<Item>) registryEntry);


        return (T) registryEntry.value();
    }


}
