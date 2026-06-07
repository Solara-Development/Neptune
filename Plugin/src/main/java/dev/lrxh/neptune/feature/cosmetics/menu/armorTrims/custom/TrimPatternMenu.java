package dev.lrxh.neptune.feature.cosmetics.menu.armorTrims.custom;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.cosmetics.custom.CustomArmorTrimData.ArmorSlot;
import dev.lrxh.neptune.feature.cosmetics.custom.CustomArmorTrimData.TrimEntry;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * PAGE 2 — Pattern selector.
 */
public class TrimPatternMenu extends Menu {

    // pattern key → smithing template Material name
    private static final String[][] PATTERNS = {
        {"bolt",      "BOLT_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"coast",     "COAST_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"dune",      "DUNE_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"eye",       "EYE_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"flow",      "FLOW_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"host",      "HOST_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"raiser",    "RAISER_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"rib",       "RIB_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"sentry",    "SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"shaper",    "SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"silence",   "SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"snout",     "SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"spire",     "SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"tide",      "TIDE_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"vex",       "VEX_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"wayfinder", "WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"ward",      "WARD_ARMOR_TRIM_SMITHING_TEMPLATE"},
        {"wild",      "WILD_ARMOR_TRIM_SMITHING_TEMPLATE"},
    };

    private final ArmorSlot armorSlot;

    public TrimPatternMenu(ArmorSlot armorSlot) {
        super(MenusLocale.ARMOR_TRIM_PATTERN_TITLE_PREFIX.getString() + armorSlot.displayName, 54, Filter.NONE);
        this.armorSlot = armorSlot;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        TrimEntry current = API.getProfile(player).getSettingData()
                .getCustomArmorTrimData().getTrimForSlot(armorSlot);

        // Background
        Material borderMat = Material.matchMaterial(MenusLocale.GUI_BORDER_MATERIAL.getString());
        if (borderMat == null) borderMat = Material.BLACK_STAINED_GLASS_PANE;
        ItemStack black = new ItemBuilder(borderMat).name(" ").build();
        ItemStack gray  = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < 54; i++) {
            final int s = i;
            final ItemStack bg = (s < 9 || s >= 45) ? black : gray;
            buttons.add(new Button(s) { @Override public ItemStack getItemStack(Player p) { return bg; }});
        }

        // Title preview shows current armor with existing trim in slot 4
        buttons.add(new Button(4) {
            @Override public ItemStack getItemStack(Player p) {
                Material mat = CustomArmorTrimMenu.armorMat(armorSlot);
                ItemStack item = new ItemBuilder(mat)
                        .name("&b" + armorSlot.displayName)
                        .lore(current != null
                                ? List.of("&7Current: &b" + CustomArmorTrimMenu.cap(current.getPattern())
                                          + " &8/ &e" + CustomArmorTrimMenu.cap(current.getMaterial()),
                                          " ", "&7Pick a new pattern below")
                                : List.of("&7No trim set yet.", " ", "&7Pick a pattern below"))
                        .build();
                if (current != null)
                    CustomArmorTrimMenu.applyTrim(item, CustomArmorTrimMenu.buildTrim(current.getPattern(), current.getMaterial()));
                return item;
            }
        });

        // Pattern buttons start at slot 9
        int slot = 9;
        for (String[] row : PATTERNS) {
            if (slot >= 45) break;
            final String patKey  = row[0];
            final String matName = row[1];
            final int btnSlot = slot++;
            final String currentPattern = current != null ? current.getPattern() : null;

            buttons.add(new Button(btnSlot) {
                @Override public ItemStack getItemStack(Player p) {
                    boolean hasPerm = p.hasPermission("neptune.cosmetics.trim.*")
                            || p.hasPermission("neptune.cosmetics.trim.pattern." + patKey);
                    boolean selected = patKey.equals(currentPattern);

                    //  smithing template item as the button icon
                    Material icon = Material.matchMaterial(matName);
                    if (icon == null) icon = Material.PAPER; // fallback icon

                    List<String> lore = new ArrayList<>();
                    lore.add("&8\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
                    if (!hasPerm) {
                        lore.add("&c\u2717 Locked");
                        lore.add("&8neptune.cosmetics.trim.pattern." + patKey);
                    } else if (selected) {
                        lore.add("&a\u2714 Currently selected");
                        lore.add(" ");
                        lore.add("&7Click to pick a different material");
                    } else {
                        lore.add("&7Click to select this pattern");
                    }

                    ItemStack item = new ItemBuilder(hasPerm ? icon : Material.GRAY_STAINED_GLASS_PANE)
                             .name((selected ? "&a\u2714 " : hasPerm ? "&b" : "&8") + CustomArmorTrimMenu.cap(patKey))
                            .lore(lore)
                            .build();

                    if (selected && hasPerm)
                        item = new ItemBuilder(item).addEnchantedGlow().build();

                    return item;
                }

                @Override public void onClick(ClickType type, Player p) {
                    boolean hasPerm = p.hasPermission("neptune.cosmetics.trim.*")
                            || p.hasPermission("neptune.cosmetics.trim.pattern." + patKey);
                    if (!hasPerm) {
                        p.sendMessage(CC.color("&cYou don't have permission for the &b"
                                + CustomArmorTrimMenu.cap(patKey) + " &cpattern!"));
                        return;
                    }
                    new TrimMaterialMenu(armorSlot, patKey).open(p);
                }
            });
        }

        // Back
        buttons.add(new Button(45) {
            @Override public ItemStack getItemStack(Player p) {
                return new ItemBuilder(Material.ARROW).name("&7\u2190 Back")
                        .lore(List.of("&7Return to armor overview")).build();
            }
            @Override public void onClick(ClickType type, Player p) {
                new CustomArmorTrimMenu().open(p);
            }
        });

        // Remove this slot
        buttons.add(new Button(49) {
            @Override public ItemStack getItemStack(Player p) {
                TrimEntry cur = API.getProfile(p).getSettingData()
                        .getCustomArmorTrimData().getTrimForSlot(armorSlot);
                if (cur == null)
                    return new ItemBuilder(Material.GRAY_DYE).name("&8No trim to remove").build();
                return new ItemBuilder(Material.BARRIER)
                        .name("&c&lRemove Trim")
                        .lore(List.of("&7Remove trim from your " + armorSlot.displayName + ".", " ", "&cClick to remove"))
                        .build();
            }
            @Override public void onClick(ClickType type, Player p) {
                API.getProfile(p).getSettingData().getCustomArmorTrimData().clearSlot(armorSlot);
                p.sendMessage(CC.color("&aRemoved trim from your &b" + armorSlot.displayName + "&a."));
                new CustomArmorTrimMenu().open(p);
            }
        });

        return buttons;
    }
}
