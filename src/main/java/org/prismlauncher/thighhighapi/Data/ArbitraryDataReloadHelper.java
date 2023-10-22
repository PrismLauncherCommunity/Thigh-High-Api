package org.prismlauncher.thighhighapi.Data;

import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.prismlauncher.thighhighapi.ThighHighs;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;

import static net.fabricmc.loader.impl.game.minecraft.Hooks.VANILLA;
import static org.prismlauncher.thighhighapi.ThighHighs.resource;

/**
 * This exists mostly to take weird vanilla/modded code out of {@link ThighHighs}
 * @see org.prismlauncher.thighhighapi.mixin.BeforeFreezeDataReloader
 */
public class ArbitraryDataReloadHelper {

    public static void importCustomDatapacksAndLoad(){
        ResourcePackManager initialResourcePackManager = ArbitraryDataReloadHelper.getResourcePackManager();


        Consumer<LifecycledResourceManager> finishReload = finishReloadWithDefaultPack;
        if(ThighHighs.loadResourcePack){
            finishReload = finishReloadWithoutDefaultPack;
        }


        doReload(initialResourcePackManager, finishReload);

    }


    @Unique
    private static Consumer<LifecycledResourceManager> finishReloadWithDefaultPack = new Consumer<LifecycledResourceManager>() {
        @Override
        public void accept(LifecycledResourceManager lifecycledResourceManager) {
            ThighHighs.SEVER_DATA_RELOAD.reload(lifecycledResourceManager);//lets go this is so dumb
        }
    };

    @Unique
    private static Consumer<LifecycledResourceManager> finishReloadWithoutDefaultPack = new Consumer<LifecycledResourceManager>() {
        @Override
        public void accept(LifecycledResourceManager manager) {


        var resources = manager.getAllResources(resource("thighhighapidefaultdata.zip"));


        for (var resource : resources){

            Path resourcePackPath = Path.of("./resourcepacks/thighhighapidefaultdata.zip");

            if (!Files.exists(resourcePackPath)) {
                try {
                    //                        Files.copy(defaultPack.getInputStream(), resourcePackPath);
                    byte[] dataPackBytes = resource.getInputStream().readAllBytes();
                    Files.write(resourcePackPath, dataPackBytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }


            ResourcePackManager newResourceManager = ArbitraryDataReloadHelper.getResourcePackManager();


            doReload(newResourceManager, finishReloadWithDefaultPack);




        }
    };


    @Unique
    private static void doReload(ResourcePackManager resourcePackManager, Consumer<LifecycledResourceManager> finishReload) {


        try {
            //make the manager find all of the existing ResourcePacks
            resourcePackManager.scanPacks();

            //grab a list of the resource pack names
            List<String> resourcePackNames = resourcePackManager.getNames().stream().toList();

            //build a NBT-string list of the resource pack names
            String names = ArbitraryDataReloadHelper.buildNbtString(resourcePackNames);
            //build a Dynamic-readable list of datapack names
            var nbtCompound2 = StringNbtReader.parse("{DataPacks:{Enabled:%s,Disabled:[]}}".formatted(names));


            //look at this cursedness - I'm using an Object to trick the comipler into thinking NbtOps.INSTANCE is a subclass of DynamicOps<NbtCompound>
            Object ops = NbtOps.INSTANCE;
            //that said, it IS an instance of DynamicOps<NbtCompound>, so there's literally no reason this should error. If it does, this is the reason and god has forsaken us.
            Dynamic<NbtCompound> dyn = new Dynamic<>((DynamicOps<NbtCompound>)ops, nbtCompound2);

            //This was stolen from Mojang code - don't blame for how weird it is. Stolen from SaveLoading#DataPacks, taken into ArbitraryDataReloader to take those methods out of ThighHighs
            ArbitraryDataReloadHelper.DataPacks dataPacks = new ArbitraryDataReloadHelper.DataPacks(resourcePackManager, ArbitraryDataReloadHelper.parseDataPackSettings(dyn), false, false);

            //yeah don't forget to actually load the datapacks...
            Pair<DataConfiguration, LifecycledResourceManager> result = dataPacks.load();//whops gotta have that lmao

            //run the supplier
            finishReload.accept(result.getSecond());


            ThighHighs.reloadServerDataAgain = false;

        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    //Mojang code - SaveLoader.DataPacks
    public record DataPacks(ResourcePackManager manager, DataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
        public Pair<DataConfiguration, LifecycledResourceManager> load() {
            FeatureSet featureSet = this.initMode ? FeatureFlags.FEATURE_MANAGER.getFeatureSet() : this.initialDataConfig.enabledFeatures();


            DataConfiguration dataConfiguration = loadDataPacks(this.manager, this.initialDataConfig.dataPacks(), this.safeMode, featureSet);


            if (!this.initMode) {
                dataConfiguration = dataConfiguration.withFeaturesAdded(dataConfiguration.enabledFeatures());
            }
            List<ResourcePack> list = this.manager.createResourcePacks();
            LifecycledResourceManagerImpl lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
            return Pair.of(dataConfiguration, lifecycledResourceManager);
        }
    }

    //again Mojang code - MinecraftServer.loadDataPacks
    public static DataConfiguration loadDataPacks(ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings, boolean safeMode, FeatureSet enabledFeatures) {
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
            ThighHighs.LOGGER.warn("Missing data pack {}", (Object)string);
        }
        for (ResourcePackProfile resourcePackProfile : resourcePackManager.getProfiles()) {
            String string2 = resourcePackProfile.getName();
            if (dataPackSettings.getDisabled().contains(string2)) continue;
            FeatureSet featureSet = resourcePackProfile.getRequestedFeatures();
            boolean bl = set.contains(string2);
            if (!bl && resourcePackProfile.getSource().canBeEnabledLater()) {
                if (featureSet.isSubsetOf(enabledFeatures)) {
                    ThighHighs.LOGGER.info("Found new data pack {}, loading it automatically", (Object)string2);
                    set.add(string2);
                } else {
                    ThighHighs.LOGGER.info("Found new data pack {}, but can't load it due to missing features {}", (Object)string2, (Object)FeatureFlags.printMissingFlags(enabledFeatures, featureSet));
                }
            }
            if (!bl || featureSet.isSubsetOf(enabledFeatures)) continue;
            ThighHighs.LOGGER.warn("Pack {} requires features {} that are not enabled for this world, disabling pack.", (Object)string2, (Object)FeatureFlags.printMissingFlags(enabledFeatures, featureSet));
            set.remove(string2);
        }
        if (set.isEmpty()) {
            ThighHighs.LOGGER.info("No datapacks selected, forcing vanilla");
            set.add(VANILLA);
        }
        resourcePackManager.setEnabledProfiles(set);
        DataPackSettings dataPackSettings2 = MinecraftServer.createDataPackSettings(resourcePackManager);
        FeatureSet featureSet2 = resourcePackManager.getRequestedFeatures();
        return new DataConfiguration(dataPackSettings2, featureSet2);
    }

    public static DataConfiguration parseDataPackSettings(Dynamic<?> dynamic) {
        return DataConfiguration.CODEC.parse(dynamic).resultOrPartial(ThighHighs.LOGGER::error).orElse(DataConfiguration.SAFE_MODE);
    }

    @NotNull
    public static ResourcePackManager getResourcePackManager() {
        var resourceType = ResourceType.SERVER_DATA;

        //load Vanilla data packs
        Path socksDataDir = Path.of("./resourcepacks");
        FileResourcePackProvider socksDataProvider = new FileResourcePackProvider(socksDataDir, resourceType, ResourcePackSource.SERVER);


        //load other mod's data packs
        @SuppressWarnings("UnstableApiUsage") var modDataPackCreator = new ModResourcePackCreator(resourceType);
        var manager = new ResourcePackManager(modDataPackCreator, socksDataProvider);

        manager.scanPacks();

        if(!manager.getNames().contains("file/thighhighapidefaultdata.zip")){
            ThighHighs.loadResourcePack = true;
        }

        //add data packs to our data pack manager
        return manager;
    }

    public static String buildNbtString(List<String> strings){
        StringBuilder names = new StringBuilder("[");
        for(String packName : strings){
            if(!names.toString().equals("[")){
                names.append(",");
            }
            names.append("\"").append(packName).append("\"");
        }
        names.append("]");
        return names.toString();
    }
}
