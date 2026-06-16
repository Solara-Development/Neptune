package dev.lrxh.neptune.feature.rankedloadout;

import dev.lrxh.neptune.feature.itembrowser.ItemBrowserService;
import dev.lrxh.neptune.game.kit.Kit;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class LoadoutWhitelistUtils {

    public static final String SECTION_PREFIX = "loadout-";

    public static String sectionFor(Kit kit) {
        return SECTION_PREFIX + kit.getName();
    }

    public static void registerBrowserSection(Kit kit) {
        ItemBrowserService.get().registerSection(sectionFor(kit), kit.getLoadoutWhitelistRaw());
    }

    public static boolean isAllowed(Kit kit, ItemStack item) {
        if (item == null || item.getType().isAir()) return true;
        Material type = item.getType();
        if (type.isLegacy() || !type.isItem()) return false;
        if (kit.getLoadoutBlacklist().contains(type)) return false;
        if (kit.getLoadoutWhitelist().isEmpty()) return true;
        return kit.getLoadoutWhitelist().contains(type);
    }

    public static List<ItemStack> sanitize(Kit kit, List<ItemStack> items) {
        if (items == null) return new ArrayList<>();
        int size = Math.max(RankedLoadout.CONTENTS_SIZE, items.size());
        ItemStack[] sanitized = new ItemStack[size];
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            if (item == null || item.getType().isAir()) {
                sanitized[i] = null;
                continue;
            }
            if (!isAllowed(kit, item)) {
                sanitized[i] = null;
                continue;
            }
            ItemStack copy = item.clone();
            int maxStack = Math.min(kit.getMaxItemStack(), copy.getMaxStackSize());
            if (copy.getAmount() > maxStack) copy.setAmount(maxStack);
            if (copy.getAmount() < 1) copy.setAmount(1);
            sanitized[i] = copy;
        }
        return new ArrayList<>(Arrays.asList(sanitized));
    }

    public static String sectionForSlot(int contentsIndex) {
        if (contentsIndex >= 36 && contentsIndex <= 39) {
            return switch (contentsIndex) {
                case 36 -> "boots";
                case 37 -> "leggings";
                case 38 -> "chestplate";
                case 39 -> "helmet";
                default -> "items";
            };
        }
        return "items";
    }

    public static boolean isWhitelistedInSection(Kit kit, Material material, String section) {
        if (!isAllowed(kit, new ItemStack(material))) return false;
        return switch (section) {
            case "helmet" -> material.name().endsWith("_HELMET") || material == Material.TURTLE_HELMET;
            case "chestplate" -> material.name().endsWith("_CHESTPLATE") || material == Material.ELYTRA;
            case "leggings" -> material.name().endsWith("_LEGGINGS");
            case "boots" -> material.name().endsWith("_BOOTS");
            default -> true;
        };
    }
}
