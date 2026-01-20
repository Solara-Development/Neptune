package dev.lrxh.neptune.feature.hotbar;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.feature.hotbar.impl.CustomItem;
import dev.lrxh.neptune.feature.hotbar.impl.Hotbar;
import dev.lrxh.neptune.feature.hotbar.impl.Item;
import dev.lrxh.neptune.feature.hotbar.impl.ItemAction;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class HotbarService extends IService {
    private static HotbarService instance;
    private final Map<ProfileState, Hotbar> items = new HashMap<>();
    private final Neptune plugin;

    public HotbarService() {
        this.plugin = Neptune.get();
    }

    public static HotbarService get() {
        if (instance == null) instance = new HotbarService();

        return instance;
    }

    Item getItem(Hotbar inventory, int slot) {
        Item[] slots = inventory.getSlots();
        if (slot >= 0 && slot < slots.length) {
            return slots[slot];
        } else {
            return null;
        }
    }

    public void giveItems(Player player) {
        player.getInventory().clear();
        ProfileState profileState = API.getProfile(player).getState();
        if (profileState.equals(ProfileState.IN_KIT_EDITOR)) return;

        Hotbar inventory = items.get(profileState);

        if (inventory != null) {
            for (int slot = 0; slot <= 8; slot++) {
                Item item = getItemForSlot(inventory, slot);

                if (item != null && item.isEnabled()) {
                    player.getInventory().setItem(item.getSlot(), item.constructItem(player.getUniqueId()));
                }
            }
        }

        player.updateInventory();
    }

    public Item getItemForSlot(Hotbar inventory, int slot) {
        return getItem(inventory, slot);
    }

    @Override
    public void load() {
        FileConfiguration config = ConfigService.get().getHotbarConfig().getConfiguration();
        if (config.getConfigurationSection("ITEMS") != null) {
            for (String section : getKeys("ITEMS")) {
                Hotbar inventory = new Hotbar();

                for (String itemName : getKeys("ITEMS." + section)) {
                    String path = "ITEMS." + section + "." + itemName + ".";

                    String displayName = config.getString(path + "NAME");
                    String material = config.getString(path + "MATERIAL");
                    List<String> lore = config.getStringList(path + "LORE");
                    boolean enabled = config.getBoolean(path + "ENABLED");
                    byte slot = (byte) config.getInt(path + "SLOT");
                    int customModelData = config.getInt(path + "CUSTOM_MODEL_DATA", 0);

                    if (!enabled) continue;

                    try {
                        Item item = new Item(ItemAction.valueOf(itemName), displayName, material, lore, true, slot, customModelData);
                        if (slot >= 0 && slot < inventory.getSlots().length) {
                            inventory.setSlot(slot, item);
                        }
                    } catch (IllegalArgumentException ignored) {
                    }

                    items.put(ProfileState.valueOf(section), inventory);
                }
            }
        }
        if (config.getConfigurationSection("CUSTOM_ITEMS") != null) {
            for (String itemName : getKeys("CUSTOM_ITEMS")) {
                String path = "CUSTOM_ITEMS." + itemName + ".";

                String displayName = config.getString(path + "NAME");
                String material = config.getString(path + "MATERIAL");
                byte slot = (byte) config.getInt(path + "SLOT");
                List<String> lore = config.getStringList(path + "LORE");
                String command = config.getString(path + "COMMAND");
                ProfileState profileState = ProfileState.valueOf(config.getString(path + "STATE"));
                int customModelData = config.getInt(path + "CUSTOM_MODEL_DATA", 0);

                CustomItem customItem = new CustomItem(displayName, material, lore, slot, command, customModelData);
                items.get(profileState).addItem(customItem, slot);
            }
        }
    }

    @Override
    public void save() {

    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getHotbarConfig();
    }
}
