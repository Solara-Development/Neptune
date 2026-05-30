package dev.lrxh.neptune.feature.itembrowser;

import dev.lrxh.api.features.IItemBrowserService;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class ItemBrowserService implements IItemBrowserService {

    private static ItemBrowserService instance;

    private final Map<String, List<Material>> sectionMaterials = new HashMap<>();
    private final Map<String, List<Material>> computed = new HashMap<>();

    public static ItemBrowserService get() {
        if (instance == null) {
            instance = new ItemBrowserService();
        }
        return instance;
    }

    @Override
    public List<Material> getItems(String section) {
        return switch (section) {
            case "blocks", "items", "helmet", "chestplate", "leggings", "boots" ->
                    computed.computeIfAbsent(section, this::computeSection);
            default -> sectionMaterials.getOrDefault(section, Collections.emptyList());
        };
    }

    private List<Material> computeSection(String section) {
        return switch (section) {
            case "blocks" -> getBlocks();
            case "items" -> getAllItems();
            case "helmet" -> bySuffix("_HELMET");
            case "chestplate" -> bySuffix("_CHESTPLATE");
            case "leggings" -> bySuffix("_LEGGINGS");
            case "boots" -> bySuffix("_BOOTS");
            default -> Collections.emptyList();
        };
    }

    public void preloadSections() {
        for (String section : new String[]{"blocks", "items", "helmet", "chestplate", "leggings", "boots"}) {
            computed.computeIfAbsent(section, this::computeSection);
        }
    }

    private List<Material> bySuffix(String suffix) {
        List<Material> list = new ArrayList<>();
        for (Material m : Material.values()) {
            if (m.isItem() && !m.isLegacy() && m.name().endsWith(suffix)) list.add(m);
        }
        return list;
    }

    public List<Material> getAllItems() {
        List<Material> list = new ArrayList<>();
        for (Material m : Material.values()) {
            if (m.isItem() && !m.isAir() && !m.isLegacy()) list.add(m);
        }
        return list;
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
        SignInputMenu.open(player, "", "Enter search", input ->
                openBrowser(player, section, itemConsumer, input, returnConsumer));
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
}
