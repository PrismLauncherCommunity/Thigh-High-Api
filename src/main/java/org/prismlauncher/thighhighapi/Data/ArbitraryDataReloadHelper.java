package org.prismlauncher.thighhighapi.Data;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import org.prismlauncher.thighhighapi.ThighHighs;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static net.fabricmc.loader.impl.game.minecraft.Hooks.VANILLA;

/**
 * This exists mostly to take weird vanilla/modded code out of {@link ThighHighs}
 * @see org.prismlauncher.thighhighapi.mixin.BeforeFreezeDataReloader
 */
public class ArbitraryDataReloadHelper {

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

        //add data packs to our data pack manager
        return new ResourcePackManager(modDataPackCreator, socksDataProvider);
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
