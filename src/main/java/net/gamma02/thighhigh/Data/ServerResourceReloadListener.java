package net.gamma02.thighhigh.Data;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.gamma02.thighhigh.Items.SockItemType;
import net.gamma02.thighhigh.ThighHighs;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

import static net.gamma02.thighhigh.ThighHighs.SockItemsToRegister;


public class ServerResourceReloadListener implements SimpleSynchronousResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    @Override
    public Identifier getFabricId() {
        return ThighHighs.resource("server_data");
    }

    @Override
    public void reload(ResourceManager manager) {
//            System.out.println("Reloading on server!");
        //make sure registries are not frozen when we try to add items to a registration list
        try {
            Registries.ITEM.createEntry(ThighHighs.TEST_SOCK);
        } catch (IllegalStateException e) {
            return;
        }


        //Collect resources that define socks - I am curious if that ID contains a file type
        Map<Identifier, Resource> resources = manager.findResources("socks", (id) -> true);

        //iterate through fetched resources
        for (Resource resource : resources.values()) {

            SockItemType item;

            try (InputStream stream = resource.getInputStream()) {

                // Consume the stream however you want, medium, rare, or well done. - well done please!
                // Import the item from json
                item = ThighHighs.ItemCreatingGSON.fromJson(new InputStreamReader(stream), SockItemType.class);

            } catch (Exception e) {
                //Log error
                System.out.println("Error occurred while loading resource json " + resource.toString() + e.getMessage());
                LOGGER.error(Arrays.toString(e.getStackTrace()));
                //don't add null item to registry! lmao
                continue;
            }
            LOGGER.info("Registering socks: " + item.name);

            SockItemsToRegister.push(item);

        }

    }
}