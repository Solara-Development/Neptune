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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * PAGE 3 — Material (ore) selector.
 * Each button is the actual ORE/INGOT item for that material.
 * Clicking immediately applies the trim and returns to the main overview.
 *
 * Permission: neptune.cosmetics.trim.material.<key>  OR  neptune.cosmetics.trim.*
 */
public class TrimMaterialMenu extends Menu {

    // material key → ore/ingot Material name → display colour
    private static final String[][] MATERIALS = {
        {"amethyst",  "AMETHYST_SHARD",    "&d"},
        {"copper",    "COPPER_INGOT",       "&6"},
        {"diamond",   "DIAMOND",            "&b"},
        {"emerald",   "EMERALD",            "&a"},
        {"gold",      "GOLD_INGOT",         "&e"},
        {"iron",      "IRON_INGOT",         "&f"},
        {"lapis",     "LAPIS_LAZULI",       "&9"},
        {"netherite", "NETHERITE_INGOT",    "&8"},
        {"quartz",    "QUARTZ",             "&f"},
        {"redstone",  "REDSTONE",           "&c"},
    };

    private final ArmorSlot armorSlot;
    private final String chosenPattern;

    public TrimMaterialMenu(ArmorSlot armorSlot, String chosenPattern) {
        super(MenusLocale.ARMOR_TRIM_MATERIAL_TITLE_PREFIX.getString() + CustomArmorTrimMenu.cap(chosenPattern), 54, Filter.NONE);
        this.armorSlot    = armorSlot;
        this.chosenPattern = chosenPattern;
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

        // Preview armor piece with chosen pattern + currently active material in slot 4
        buttons.add(new Button(4) {
            @Override public ItemStack getItemStack(Player p) {
                Material mat = CustomArmorTrimMenu.armorMat(armorSlot);
                String previewMat = (current != null) ? current.getMaterial() : "amethyst";
                ItemStack item = new ItemBuilder(mat)
                        .name("&b" + armorSlot.displayName + " &8| &7" + CustomArmorTrimMenu.cap(chosenPattern))
                        .lore(List.of("&7Pattern: &b" + CustomArmorTrimMenu.cap(chosenPattern),
                                " ", "&7Select an ore to apply the trim"))
                        .build();
                CustomArmorTrimMenu.applyTrim(item, CustomArmorTrimMenu.buildTrim(chosenPattern, previewMat));
                return item;
            }
        });

        // Material buttons slots 9..18
        int slot = 9;
        for (String[] row : MATERIALS) {
            if (slot >= 45) break;
            final String matKey     = row[0];
            final String iconName   = row[1];
            final String colour     = row[2];
            final int btnSlot = slot++;
            final String curMat = (current != null && chosenPattern.equals(current.getPattern()))
                    ? current.getMaterial() : null;

            buttons.add(new Button(btnSlot) {
                @Override public ItemStack getItemStack(Player p) {
                    boolean hasPerm = p.hasPermission("neptune.cosmetics.trim.*")
                            || p.hasPermission("neptune.cosmetics.trim.material." + matKey);
                    boolean selected = matKey.equals(curMat);

                    Material icon = Material.matchMaterial(iconName);
                    if (icon == null) icon = Material.PAPER;

                    List<String> lore = new ArrayList<>();
                    lore.add("&8\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
                    lore.add("&7Pattern:  &b" + CustomArmorTrimMenu.cap(chosenPattern));
                    lore.add("&7Material: " + colour + CustomArmorTrimMenu.cap(matKey));
                    lore.add(" ");
                    if (!hasPerm) {
                        lore.add("&c\u2717 Locked");
                        lore.add("&8neptune.cosmetics.trim.material." + matKey);
                    } else if (selected) {
                        lore.add("&a\u2714 Currently applied");
                    } else {
                        lore.add("&aClick to apply this trim");
                    }

                    ItemStack item = new ItemBuilder(hasPerm ? icon : Material.GRAY_STAINED_GLASS_PANE)
                            .name((selected ? "&a\u2714 " : hasPerm ? colour : "&8") + CustomArmorTrimMenu.cap(matKey))
                            .lore(lore)
                            .build();

                    if (selected && hasPerm)
                        item = new ItemBuilder(item).addEnchantedGlow().build();

                    return item;
                }

                @Override public void onClick(ClickType type, Player p) {
                    boolean hasPerm = p.hasPermission("neptune.cosmetics.trim.*")
                            || p.hasPermission("neptune.cosmetics.trim.material." + matKey);
                    if (!hasPerm) {
                        p.sendMessage(CC.color("&cYou don't have permission for &e"
                                + CustomArmorTrimMenu.cap(matKey) + " &cmaterial!"));
                        return;
                    }
                    CustomArmorTrimData trimData = API.getProfile(p).getSettingData().getCustomArmorTrimData();
                    trimData.setTrimForSlot(armorSlot, new TrimEntry(chosenPattern, matKey));
                    trimData.setActive(true);

                    p.sendMessage(CC.color("&aApplied &b" + CustomArmorTrimMenu.cap(chosenPattern)
                            + " &7/ " + colour + CustomArmorTrimMenu.cap(matKey)
                            + " &atrim to your &b" + armorSlot.displayName + "&a!"));

                    new CustomArmorTrimMenu().open(p);
                }
            });
        }

        // Back to pattern picker
        buttons.add(new Button(45) {
            @Override public ItemStack getItemStack(Player p) {
                return new ItemBuilder(Material.ARROW).name("&7\u2190 Back to Patterns")
                        .lore(List.of("&7Go back to pattern selection")).build();
            }
            @Override public void onClick(ClickType type, Player p) {
                new TrimPatternMenu(armorSlot).open(p);
            }
        });

        return buttons;
    }
}
