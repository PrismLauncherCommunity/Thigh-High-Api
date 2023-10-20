package net.gamma02.thighhigh;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.util.TriState;
import net.gamma02.thighhigh.Items.SockItem;
import net.gamma02.thighhigh.Items.SockItemDesearlizer;
import net.gamma02.thighhigh.Items.SockItemType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static dev.emi.trinkets.TrinketsMain.LOGGER;
import static net.fabricmc.loader.impl.game.minecraft.Hooks.VANILLA;
import static net.minecraft.nbt.NbtOps.INSTANCE;

public class ThighHighs implements ModInitializer {



    public static String modid = "thighhigh";

    public static Logger MOD_LOGGER = LogUtils.getLogger();
    private static Logger LOGGER = LogUtils.getLogger();

    public static String MODID = modid;

    public static boolean reloadServerDataAgain = true;

    public static boolean reloadClientDataAgain = true;




    public static TagKey<Item> socks = TagKey.of(RegistryKeys.ITEM, new Identifier("trinkets", "items/legs/socks"));

    public static Identifier resource(String path){
        return new Identifier(modid, path);
    }

    public static ArrayDeque<SockItemType> SockItemsToRegister = new ArrayDeque<>();

    public static ArrayList<RegistryEntry.Reference<Item>> socksToAddToTag = new ArrayList<>();

    public static HashMap<Identifier, SockItemType> RegisteredSocks = new HashMap<>();

    public static String socksDirectory = "thighhighs/socks";




    public static SockItemType TEST_SOCK = new SockItemType(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1), "socks", "test_socks");

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

//            manager.findResources();
            var resources = manager.findResources("socks", (id) -> true);


            for(Resource resource : resources.values()) {
                try(InputStream stream = resource.getInputStream()) {
                    // Consume the stream however you want, medium, rare, or well done.
                    var item = gson.fromJson(new InputStreamReader(stream), SockItemType.class);

                    System.out.println("Registering socks: " + item.name);

                    SockItemsToRegister.push(item);
                } catch(Exception e) {
                    System.out.println("Error occurred while loading resource json " + resource.toString() + e.getMessage());
                    e.printStackTrace();
                }

            }
//            var resource = manager.getResource(resource("socks/test_json_sock.json"));
//            try(InputStream stream = resource.get().getInputStream()) {
//                // Consume the stream however you want, medium, rare, or well done.
//                var item = gson.fromJson(new InputStreamReader(stream), SockItemType.class);
//
//                System.out.println("Registering socks: " + item.name);
//
//                SockItemsToRegister.add(item);
//            } catch(Exception e) {
//                System.out.println("Error occurred while loading resource json " + resource.toString() + e.getMessage());
//            }





        }
    };

    public static SimpleSynchronousResourceReloadListener CLIENT_RESOURCE_RELOAD = new SimpleSynchronousResourceReloadListener() {
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
            System.out.println("REGISTRY NOT FROZEN! Reloading assets/resource");




        }
    };



    public ThighHighs(){

        TrinketsApi.registerTrinketPredicate(resource("is_sock"), (stacl, ref, entity) -> {
            return TriState.of(stacl.getItem() instanceof SockItem && ref.inventory().getSlotType().getName().contains("socks"));
        });

        RegisteredSocks.put(resource("test_socks"), TEST_SOCK);

        registerAddTags(TEST_SOCK, Registries.ITEM, resource("test_socks"), socks);




        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SEVER_DATA_RELOAD);



        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(CLIENT_RESOURCE_RELOAD);

    }




    @Override
    public void onInitialize() {

//        System.out.println("Doing stuff!");

        //socks directory:

//        if(!SockItemsToRegister.isEmpty()) {


//        ArrayList<Identifier> socksToAddToTag = new ArrayList<>();

            //Register socks defined in JSON and add them to the socks trinket tag.
            while (!SockItemsToRegister.isEmpty()) {
                var sock = SockItemsToRegister.pop();

                Identifier sockId = sock.name;

//                RegisteredSocks.put(sockId, sock);



                LOGGER.info(sockId.toString());

                SockItemType registered = registerAddTags(sock, Registries.ITEM, sockId, socks);;//SHOULD BE THIS
//                SockItemsToRegister.pop()

                RegisteredSocks.replace(sockId, registered);

                TrinketsApi.registerTrinket(registered, registered);
//            socksToAddToTag.add(sockId);
            }
//        }


        TrinketsApi.registerTrinket(TEST_SOCK, TEST_SOCK);
    }



    @SafeVarargs
    public static <A, T extends A> T registerAddTags(T object, Registry<A> registry, Identifier id, TagKey<A>... tags){
        T registryEntry = Registry.register(registry, id.toString(), object);
//        ArrayList<TagKey<A>> oldTags = new ArrayList<>(registryEntry.streamTags().toList());
//        oldTags.addAll(Arrays.stream(tags).toList());
//        registryEntry.setTags(oldTags);
//        if(object instanceof SockItem i)
//            socksToAddToTag.add(registry.getEntry(registryEntry));


        return registryEntry;
    }


    public record DataPacks(ResourcePackManager manager, DataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
        public Pair<DataConfiguration, LifecycledResourceManager> load() {
            FeatureSet featureSet = this.initMode ? FeatureFlags.FEATURE_MANAGER.getFeatureSet() : this.initialDataConfig.enabledFeatures();


            DataConfiguration dataConfiguration = loadDataPacks(this, this.manager, this.initialDataConfig.dataPacks(), this.safeMode, featureSet);


            if (!this.initMode) {
                dataConfiguration = dataConfiguration.withFeaturesAdded(dataConfiguration.enabledFeatures());
            }
            List<ResourcePack> list = this.manager.createResourcePacks();
            LifecycledResourceManagerImpl lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
            return Pair.of(dataConfiguration, lifecycledResourceManager);
        }
    }

    public static DataConfiguration loadDataPacks(DataPacks packs, ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings, boolean safeMode, FeatureSet enabledFeatures) {
        resourcePackManager.scanPacks();
        if (safeMode) {
            resourcePackManager.setEnabledProfiles(Collections.singleton(VANILLA));
            return DataConfiguration.SAFE_MODE;
        }

        LinkedHashSet<String> set = Sets.newLinkedHashSet();
        for (String string : dataPackSettings.getEnabled()) {
            if (resourcePackManager.hasProfile(string)) {
                set.add(string);
                continue;
            }
            LOGGER.warn("Missing data pack {}", (Object)string);
        }
        for (ResourcePackProfile resourcePackProfile : resourcePackManager.getProfiles()) {
            String string2 = resourcePackProfile.getName();
            if (dataPackSettings.getDisabled().contains(string2)) continue;
            FeatureSet featureSet = resourcePackProfile.getRequestedFeatures();
            boolean bl = set.contains(string2);
            if (!bl && resourcePackProfile.getSource().canBeEnabledLater()) {
                if (featureSet.isSubsetOf(enabledFeatures)) {
                    LOGGER.info("Found new data pack {}, loading it automatically", (Object)string2);
                    set.add(string2);
                } else {
                    LOGGER.info("Found new data pack {}, but can't load it due to missing features {}", (Object)string2, (Object)FeatureFlags.printMissingFlags(enabledFeatures, featureSet));
                }
            }
            if (!bl || featureSet.isSubsetOf(enabledFeatures)) continue;
            LOGGER.warn("Pack {} requires features {} that are not enabled for this world, disabling pack.", (Object)string2, (Object)FeatureFlags.printMissingFlags(enabledFeatures, featureSet));
            set.remove(string2);
        }
        if (set.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            set.add(VANILLA);
        }
        resourcePackManager.setEnabledProfiles(set);
        DataPackSettings dataPackSettings2 = MinecraftServer.createDataPackSettings(resourcePackManager);
        FeatureSet featureSet2 = resourcePackManager.getRequestedFeatures();
        return new DataConfiguration(dataPackSettings2, featureSet2);
    }

    public static DataConfiguration parseDataPackSettings(Dynamic<?> dynamic) {
        return DataConfiguration.CODEC.parse(dynamic).resultOrPartial(LOGGER/*i stole your logger >:D*/::error).orElse(DataConfiguration.SAFE_MODE);
    }


}
