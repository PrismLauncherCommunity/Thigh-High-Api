package org.prismlauncher.thighhighapi.Data;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.prismlauncher.thighhighapi.ThighHighs.resource;

/**
 * We might need this someday.
 */
public class ClientResourceReloadListener implements SimpleSynchronousResourceReloadListener{

    public static final Logger LOGGER = LogUtils.getLogger();


    //supply the ID of this listener to the resource manager
    @Override
    public Identifier getFabricId() {
        return resource("client_data");
    }

    //listen to reloads and keep track of if the ItemRegistry is frozen yet
    @Override
    public void reload(ResourceManager manager) {

        try {
            ((SimpleRegistry<Item>)Registries.ITEM).assertNotFrozen();
            LOGGER.debug("Registry not frozen during reload of assets");
        } catch (IllegalStateException e){
            LOGGER.debug("Item registry frozen during reload of assets!");
        }

        var resource = manager.getResource(resource("thighhighapidefaultdata.zip"));

        if(resource.isPresent()){
            Resource defaultPack = resource.get();
            Path resourcePackPath = Path.of("./resourcepacks/thighhighapidefaultdata.zip");

            if(!Files.exists(resourcePackPath)){
                try {
                    Files.copy(defaultPack.getInputStream(), resourcePackPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }








    }
}
