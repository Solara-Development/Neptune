package dev.lrxh.neptune.feature.itembrowser;

import dev.lrxh.api.features.IItemBrowserService;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class ItemBrowserService implements IItemBrowserService {

    private static ItemBrowserService instance;

    private final Map<String, List<Material>> sectionMaterials = new HashMap<>();
    private final Map<UUID, SearchSession> searchSessions = new HashMap<>();

    public static ItemBrowserService get() {
        if (instance == null) {
            instance = new ItemBrowserService();
        }
        return instance;
    }

    @Override
    public List<Material> getItems(String section) {
        if (section.equals("blocks")) return getBlocks();
        return sectionMaterials.getOrDefault(section, Collections.emptyList());
    }

    public List<Material> getBlocks() {
        List<Material> list = new ArrayList<>();

        for (BlockType block : Registry.BLOCK) {
            Material m = block.asMaterial();
            if (m != null && !m.isAir()) {
                list.add(m);
            }
        }
        return list;
    }

    @Override
    public void openBrowser(Player player, String section, Consumer<Material> itemConsumer, Runnable returnConsumer) {
        openBrowser(player, section, itemConsumer, "", returnConsumer);
    }

    public void openBrowser(Player player, String section, Consumer<Material> itemConsumer, String search, Runnable returnConsumer) {
        new ItemBrowserMenu(get(), section, itemConsumer, search, returnConsumer).open(player);
    }

    public void requestSearch(Player player, String section, Consumer<Material> itemConsumer, Runnable returnConsumer) {
        player.closeInventory();
        player.sendMessage("§ePlease type your search in chat.");
        searchSessions.put(player.getUniqueId(), new SearchSession(section, itemConsumer, returnConsumer));
    }

    public SearchSession removeSearchSession(UUID uuid) {
        return searchSessions.remove(uuid);
    }

    @Override
    public void registerSection(String section, List<String> materialNames) {
        List<Material> materials = new ArrayList<>();
        for (String matName : materialNames) {
            Material mat = Material.matchMaterial(matName);
            if (mat != null && mat.isItem()) {
                materials.add(mat);
            }
        }
        sectionMaterials.put(section, materials);
    }

    record SearchSession(String section, Consumer<Material> itemConsumer, Runnable returnConsumer) {
    }
}
