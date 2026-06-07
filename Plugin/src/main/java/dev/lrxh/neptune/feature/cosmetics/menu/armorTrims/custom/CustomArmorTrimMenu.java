package dev.lrxh.neptune.feature.cosmetics.menu.armorTrims.custom;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.cosmetics.custom.CustomArmorTrimData;
import dev.lrxh.neptune.feature.cosmetics.custom.CustomArmorTrimData.ArmorSlot;
import dev.lrxh.neptune.feature.cosmetics.custom.CustomArmorTrimData.TrimEntry;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * PAGE 1 — Four armor pieces displayed with their current trim.
 *
 * 54-slot layout:
 *   Row 1 (0-8)   : black glass border
 *   Row 2 (9-17)  : [black][cyan][HELMET@10][cyan][CHEST@12][cyan][LEGS@14][cyan][BOOTS@16][black]
 *   Row 3 (18-26) : black glass border  (info label @ 22)
 *   Row 4 (27-35) : black glass border
 *   Row 5 (36-44) : black glass border
 *   Row 6 (45-53) : black glass  + CLEAR ALL @ 49
 *
 * Each armor piece shows the NETHERITE variant so trim is visible.
 * No trim set   → bare netherite armor, lore says "No trim applied".
 * Trim is set   → netherite armor with that exact trim rendered.
 * Left-click    → TrimPatternMenu (page 2)
 * Right-click   → remove trim for that slot
 */
public class CustomArmorTrimMenu extends Menu {

    public CustomArmorTrimMenu() {
        super(MenusLocale.ARMOR_TRIM_MENU_TITLE.getString(), 54, Filter.NONE);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        CustomArmorTrimData data = API.getProfile(player).getSettingData().getCustomArmorTrimData();

        // fill entire GUI with configurable border glass
        Material borderMatRaw = Material.matchMaterial(MenusLocale.GUI_BORDER_MATERIAL.getString());
        if (borderMatRaw == null) borderMatRaw = Material.BLACK_STAINED_GLASS_PANE;
        Material accentMatRaw = Material.matchMaterial(MenusLocale.GUI_ACCENT_MATERIAL.getString());
        if (accentMatRaw == null) accentMatRaw = Material.CYAN_STAINED_GLASS_PANE;
        final Material borderFinal = borderMatRaw;
        final Material accentFinal = accentMatRaw;
        ItemStack black = new ItemBuilder(borderFinal).name(" ").build();
        ItemStack cyan  = new ItemBuilder(accentFinal).name(" ").build();
        for (int i = 0; i < 54; i++) {
            final ItemStack bg = black;
            final int s = i;
            buttons.add(new Button(s) { @Override public ItemStack getItemStack(Player p) { return bg; }});
        }
        // cyan accents between the armor slots
        for (int accent : new int[]{9, 11, 13, 15, 17}) {
            final ItemStack cv = cyan;
            buttons.add(new Button(accent) { @Override public ItemStack getItemStack(Player p) { return cv; }});
        }

        // ── 4 armor piece buttons ─────────────────────────────────────────────
        buttons.add(armorButton(ArmorSlot.HELMET,     10, data));
        buttons.add(armorButton(ArmorSlot.CHESTPLATE, 12, data));
        buttons.add(armorButton(ArmorSlot.LEGGINGS,   14, data));
        buttons.add(armorButton(ArmorSlot.BOOTS,      16, data));

        // ── Info / hint button at centre (slot 22) ────────────────────────────
        int infoSlot = MenusLocale.ARMOR_TRIM_INFO_SLOT.getInt();
        buttons.add(new Button(infoSlot) {
            @Override public ItemStack getItemStack(Player p) {
                Material m = Material.matchMaterial(MenusLocale.ARMOR_TRIM_INFO_MATERIAL.getString());
                if (m == null) m = Material.BOOK;
                return new ItemBuilder(m)
                        .name(MenusLocale.ARMOR_TRIM_INFO_NAME.getString())
                        .lore(MenusLocale.ARMOR_TRIM_INFO_LORE.getStringList())
                        .build();
            }
        });

        // ── Clear-all button (configurable slot) ─────────────────────────────
        int clearSlot = MenusLocale.ARMOR_TRIM_CLEAR_SLOT.getInt();
        buttons.add(new Button(clearSlot) {
            @Override public ItemStack getItemStack(Player p) {
                boolean hasAny = data.hasAnyTrim();
                Material m = Material.matchMaterial(hasAny
                        ? MenusLocale.ARMOR_TRIM_CLEAR_MATERIAL_HAS.getString()
                        : MenusLocale.ARMOR_TRIM_CLEAR_MATERIAL_EMPTY.getString());
                if (m == null) m = hasAny ? Material.BARRIER : Material.GRAY_STAINED_GLASS_PANE;
                return new ItemBuilder(m)
                        .name(hasAny
                                ? MenusLocale.ARMOR_TRIM_CLEAR_NAME_HAS.getString()
                                : MenusLocale.ARMOR_TRIM_CLEAR_NAME_EMPTY.getString())
                        .lore(hasAny
                                ? MenusLocale.ARMOR_TRIM_CLEAR_LORE_HAS.getStringList()
                                : MenusLocale.ARMOR_TRIM_CLEAR_LORE_EMPTY.getStringList())
                        .build();
            }
            @Override public void onClick(ClickType type, Player p) {
                if (!API.getProfile(p).getSettingData().getCustomArmorTrimData().hasAnyTrim()) return;
                API.getProfile(p).getSettingData().getCustomArmorTrimData().clearAll();
                p.sendMessage(CC.color("&aAll armor trims cleared."));
                new CustomArmorTrimMenu().open(p);
            }
        });

        return buttons;
    }

    // ── Armor piece button ─────────────────────────────────────────────────────
    private Button armorButton(ArmorSlot slot, int guiSlot, CustomArmorTrimData data) {
        return new Button(guiSlot) {
            @Override public ItemStack getItemStack(Player p) {
                TrimEntry entry = data.getTrimForSlot(slot);
                boolean hasPerm = hasTrimPermission(p, slot);

                Material mat = armorMat(slot);
                List<String> lore = new ArrayList<>();
                lore.add("&8\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
                if (!hasPerm) {
                    lore.add("&c\u2717 No Permission");
                    lore.add("&8neptune.cosmetics.trim." + slot.name().toLowerCase());
                } else if (entry != null) {
                    lore.add("&7Pattern:  &b" + cap(entry.getPattern()));
                    lore.add("&7Material: &e" + cap(entry.getMaterial()));
                    lore.add(" ");
                    lore.add("&aLeft-click &7\u2192 change trim");
                    lore.add("&cRight-click &7\u2192 remove trim");
                } else {
                    lore.add("&7No trim applied.");
                    lore.add(" ");
                    lore.add("&aClick &7to add a trim");
                }

                ItemStack item = new ItemBuilder(mat)
                        .name((hasPerm ? "&b&l" : "&8&l") + slot.displayName)
                        .lore(lore)
                        .build();

                // Apply visual trim if set
                if (entry != null && hasPerm) {
                    ArmorTrim trim = buildTrim(entry.getPattern(), entry.getMaterial());
                    applyTrim(item, trim);
                }
                return item;
            }

            @Override public void onClick(ClickType type, Player p) {
                if (!hasTrimPermission(p, slot)) {
                    p.sendMessage(CC.color("&cNo permission for &b" + slot.displayName + "&c!"));
                    return;
                }
                if (type == ClickType.RIGHT) {
                    API.getProfile(p).getSettingData().getCustomArmorTrimData().clearSlot(slot);
                    p.sendMessage(CC.color("&aRemoved trim from your &b" + slot.displayName + "&a."));
                    new CustomArmorTrimMenu().open(p);
                } else {
                    new TrimPatternMenu(slot).open(p);
                }
            }
        };
    }

    // ── Package-private helpers ────────────────────────────────────────────────
    static boolean hasTrimPermission(Player p, ArmorSlot slot) {
        return p.hasPermission("neptune.cosmetics.trim.*")
                || p.hasPermission("neptune.cosmetics.trim." + slot.name().toLowerCase());
    }

    static ArmorTrim buildTrim(String patternKey, String materialKey) {
        try {
            TrimPattern tp = Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(patternKey));
            TrimMaterial tm = Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(materialKey));
            if (tp == null || tm == null) return null;
            return new ArmorTrim(tm, tp);
        } catch (Exception ignored) { return null; }
    }

    static void applyTrim(ItemStack item, ArmorTrim trim) {
        if (item == null || trim == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof ArmorMeta am) { am.setTrim(trim); item.setItemMeta(am); }
    }

    static Material armorMat(ArmorSlot slot) {
        Material m = Material.matchMaterial(slot.defaultMaterial);
        return m != null ? m : Material.NETHERITE_CHESTPLATE;
    }

    static String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).replace("_", " ");
    }
}
