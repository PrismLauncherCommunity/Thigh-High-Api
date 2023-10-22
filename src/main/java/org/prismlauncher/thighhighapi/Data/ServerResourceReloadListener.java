package org.prismlauncher.thighhighapi.Data;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.prismlauncher.thighhighapi.Compat.Trinkets;
import org.prismlauncher.thighhighapi.Items.SockItemType;
import org.prismlauncher.thighhighapi.ThighHighs;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;


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
            ((SimpleRegistry<Item>)Registries.ITEM).assertNotFrozen();
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

//                var entry = Registries.ITEM.createEntry(item);

            } catch (Exception e) {
                //Log error
                System.out.println("Error occurred while loading resource json " + resource.toString() + e.getMessage());
                LOGGER.error(Arrays.toString(e.getStackTrace()));
                //don't add null item to registry! lmao
                continue;
            }
            LOGGER.info("Registering socks: " + item.name);

//            var sock = SockItemsToRegister.pop();

            Identifier sockId = item.name;


//            LOGGER.info(sockId.toString());

            SockItemType registered = ThighHighs.register(item, Registries.ITEM, sockId);//SHOULD BE THIS
//            SockItemType registered = Registry.register(Registries.ITEM, sockId, item)

            ThighHighs.RegisteredSocks.put(sockId, registered);

            Trinkets.registerSockTrinket(registered);



        }

    }
}