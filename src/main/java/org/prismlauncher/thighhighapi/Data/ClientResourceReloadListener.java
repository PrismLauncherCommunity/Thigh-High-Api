package org.prismlauncher.thighhighapi.Data;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import org.prismlauncher.thighhighapi.ThighHighs;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

/**
 * We might need this someday.
 */
public class ClientResourceReloadListener implements SimpleSynchronousResourceReloadListener{

    public static final Logger LOGGER = LogUtils.getLogger();


    //supply the ID of this listener to the resource manager
    @Override
    public Identifier getFabricId() {
        return ThighHighs.resource("client_data");
    }

    //listen to reloads and keep track of if the ItemRegistry is frozen yet
    @Override
    public void reload(ResourceManager manager) {

        try{
            Registries.ITEM.createEntry(ThighHighs.TEST_SOCK);
        }catch (IllegalStateException e){
            LOGGER.debug("Item registry frozen during reload of assets/resource!");
            return;
        }
        LOGGER.debug("Registry not frozen during reload of assets/resource");




    }
}
