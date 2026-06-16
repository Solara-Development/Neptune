package dev.lrxh.neptune.feature.itembrowser;

import dev.lrxh.api.features.IItemBrowserService;
import dev.lrxh.neptune.configs.impl.SignsLocale;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.function.Consumer;

public class ItemBrowserService implements IItemBrowserService {

    private static final Set<Material> POTION_MATERIALS = Set.of(
            Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION, Material.TIPPED_ARROW);

    private static final Set<String> FIREWORK_SUFFIXES = Set.of("_1", "_2", "_3");

    private static ItemBrowserService instance;

    private final Map<String, List<Material>> sectionMaterials = new HashMap<>();
    private final Map<String, List<String>> sectionRawPatterns = new HashMap<>();
    private final Map<String, List<ItemStack>> computed = new HashMap<>();

    public static ItemBrowserService get() {
        if (instance == null) {
            instance = new ItemBrowserService();
        }
        return instance;
    }

    @Override
    public List<Material> getItems(String section) {
        return getItemStacks(section).stream().map(ItemStack::getType).distinct().toList();
    }

    public List<ItemStack> getItemStacks(String section) {
        return switch (section) {
            case "blocks", "items", "helmet", "chestplate", "leggings", "boots" ->
                    computed.computeIfAbsent(section, this::computeSection);
            default -> computeCustomSection(section);
        };
    }

    private List<ItemStack> computeCustomSection(String section) {
        List<ItemStack> list = new ArrayList<>();
        List<Material> materials = sectionMaterials.getOrDefault(section, Collections.emptyList());
        List<String> rawPatterns = sectionRawPatterns.getOrDefault(section, Collections.emptyList());

        // First, add all standard materials (with potion expansion)
        for (Material m : materials) {
            if (!m.isItem()) continue;
            if (POTION_MATERIALS.contains(m)) {
                for (PotionType type : PotionType.values()) {
                    ItemStack stack = new ItemStack(m);
                    if (stack.getItemMeta() instanceof PotionMeta meta) {
                        meta.setBasePotionType(type);
                        stack.setItemMeta(meta);
                    }
                    list.add(stack);
                }
            } else {
                list.add(new ItemStack(m));
            }
        }

        // Next, process raw patterns for special items like FIREWORK_ROCKET_1/2/3
        for (String raw : rawPatterns) {
            if (raw.startsWith("FIREWORK_ROCKET_")) {
                try {
                    int power = Integer.parseInt(raw.substring("FIREWORK_ROCKET_".length()));
                    if (power < 1 || power > 3) continue;
                    ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
                    FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
                    if (meta != null) {
                        meta.setPower(power);
                        firework.setItemMeta(meta);
                    }
                    list.add(firework);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return list;
    }

    private List<ItemStack> computeSection(String section) {
        List<Material> materials = switch (section) {
            case "blocks" -> getBlocks();
            case "items" -> getAllItems();
            case "helmet" -> bySuffix("_HELMET");
            case "chestplate" -> bySuffix("_CHESTPLATE");
            case "leggings" -> bySuffix("_LEGGINGS");
            case "boots" -> bySuffix("_BOOTS");
            default -> Collections.emptyList();
        };
        List<ItemStack> list = new ArrayList<>();
        for (Material m : materials) {
            if (!m.isItem()) continue;
            if (POTION_MATERIALS.contains(m)) {
                for (PotionType type : PotionType.values()) {
                    ItemStack stack = new ItemStack(m);
                    if (stack.getItemMeta() instanceof PotionMeta meta) {
                        meta.setBasePotionType(type);
                        stack.setItemMeta(meta);
                    }
                    list.add(stack);
                }
            } else {
                list.add(new ItemStack(m));
            }
        }
        return list;
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
        openItemBrowser(player, section, item -> itemConsumer.accept(item.getType()), "", returnConsumer);
    }

    public void openItemBrowser(Player player, String section, Consumer<ItemStack> itemConsumer, Runnable returnConsumer) {
        openItemBrowser(player, section, itemConsumer, "", returnConsumer);
    }

    public void openItemBrowser(Player player, String section, Consumer<ItemStack> itemConsumer, String search, Runnable returnConsumer) {
        new ItemBrowserMenu(get(), section, itemConsumer, search, returnConsumer).open(player);
    }

    public void requestSearch(Player player, String section, Consumer<ItemStack> itemConsumer, Runnable returnConsumer) {
        player.closeInventory();
        SignInputMenu.open(player, "", SignsLocale.ITEM_BROWSER_SEARCH.getStringList(), input ->
                openItemBrowser(player, section, itemConsumer, input, returnConsumer));
    }

    @Override
    public void registerSection(String section, List<String> rawNames) {
        List<Material> materials = new ArrayList<>();
        List<String> patterns = new ArrayList<>();
        for (String raw : rawNames) {
            // Check for firework rocket patterns: FIREWORK_ROCKET_1, FIREWORK_ROCKET_2, FIREWORK_ROCKET_3
            if (raw.startsWith("FIREWORK_ROCKET_") && FIREWORK_SUFFIXES.contains("_" + raw.substring("FIREWORK_ROCKET_".length()))) {
                patterns.add(raw);
                continue;
            }
            Material mat = Material.matchMaterial(raw);
            if (mat != null && mat.isItem()) {
                materials.add(mat);
            }
        }
        sectionMaterials.put(section, materials);
        sectionRawPatterns.put(section, patterns);
    }
}