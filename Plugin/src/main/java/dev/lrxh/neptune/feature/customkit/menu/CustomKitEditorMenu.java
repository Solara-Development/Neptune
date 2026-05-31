package dev.lrxh.neptune.feature.customkit.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SignsLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.feature.customkit.enchant.EnchantmentBrowserMenu;
import dev.lrxh.neptune.feature.itembrowser.ItemBrowserService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomKitEditorMenu extends Menu {
    private final CustomKit kit;

    public CustomKitEditorMenu(CustomKit kit) {
        super(MenusLocale.CUSTOM_KIT_EDITOR_TITLE.getString(), MenusLocale.CUSTOM_KIT_EDITOR_SIZE.getInt(), Filter.FILL);
        this.kit = kit;
    }

    private static boolean hasEnchantments(ItemStack item) {
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.canEnchantItem(item)) return true;
        }
        return false;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (int es = 0; es <= 35; es++) {
            int ci = es <= 26 ? es + 9 : es - 27;
            buttons.add(slot(es, ci, MenusLocale.CUSTOM_KIT_EDITOR_EMPTY_MATERIAL.getString(),
                    MenusLocale.CUSTOM_KIT_EDITOR_EMPTY_NAME.getString(), "items"));
        }
        String armor = MenusLocale.CUSTOM_KIT_EDITOR_SLOT_MATERIAL.getString();
        buttons.add(slot(45, 39, armor, MenusLocale.CUSTOM_KIT_EDITOR_HELMET_NAME.getString(), "helmet"));
        buttons.add(slot(46, 38, armor, MenusLocale.CUSTOM_KIT_EDITOR_CHESTPLATE_NAME.getString(), "chestplate"));
        buttons.add(slot(47, 37, armor, MenusLocale.CUSTOM_KIT_EDITOR_LEGGINGS_NAME.getString(), "leggings"));
        buttons.add(slot(48, 36, armor, MenusLocale.CUSTOM_KIT_EDITOR_BOOTS_NAME.getString(), "boots"));
        buttons.add(slot(50, 40, armor, MenusLocale.CUSTOM_KIT_EDITOR_OFFHAND_NAME.getString(), "items"));

        buttons.add(new ReturnButton(53, new CustomKitManageMenu(kit)));
        return buttons;
    }

    private Button slot(int editorSlot, int contentsIndex, String placeholder, String label, String section) {
        return new Button(editorSlot) {
            @Override
            public ItemStack getItemStack(Player p) {
                ItemStack item = kit.itemAt(contentsIndex);
                if (item != null && !item.getType().isAir()) return withHintLore(item);
                return new ItemBuilder(placeholder).name(label)
                        .lore(MenusLocale.CUSTOM_KIT_EDITOR_ADD_LORE.getStringList()).build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                ItemStack item = kit.itemAt(contentsIndex);
                if (item != null && !item.getType().isAir()) {
                    if (type == ClickType.SHIFT_RIGHT) {
                        if (item.getMaxStackSize() <= 1) {
                            MessagesLocale.CUSTOM_KIT_CANT_STACK.send(p.getUniqueId());
                            return;
                        }
                        int max = item.getMaxStackSize();
                        p.closeInventory();
                        SignInputMenu.open(p, "", SignsLocale.CUSTOM_KIT_AMOUNT.getStringList(), input -> {
                            try {
                                item.setAmount(Math.max(1, Math.min(max, Integer.parseInt(input.trim()))));
                                kit.setItemAt(contentsIndex, item);
                                save(p);
                            } catch (NumberFormatException ignored) {
                            }
                            new CustomKitEditorMenu(kit).open(p);
                        });
                    } else if (type.isRightClick()) {
                        kit.setItemAt(contentsIndex, null);
                        save(p);
                        open(p);
                    } else if (hasEnchantments(item)) {
                        new EnchantmentBrowserMenu(kit, contentsIndex).open(p);
                    } else {
                        MessagesLocale.CUSTOM_KIT_CANT_ENCHANT.send(p.getUniqueId());
                    }
                    return;
                }
                ItemBrowserService.get().openItemBrowser(p, section, selected -> {
                    kit.setItemAt(contentsIndex, selected);
                    save(p);
                    new CustomKitEditorMenu(kit).open(p);
                }, () -> new CustomKitEditorMenu(kit).open(p));
            }
        };
    }

    private ItemStack withHintLore(ItemStack item) {
        ItemStack display = item.clone();
        ItemMeta meta = display.getItemMeta();
        if (meta != null) {
            List<Component> lore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
            for (String line : MenusLocale.CUSTOM_KIT_EDITOR_ITEM_LORE.getStringList()) {
                lore.add(CC.color(line).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .colorIfAbsent(NamedTextColor.WHITE));
            }
            meta.lore(lore);
            display.setItemMeta(meta);
        }
        return display;
    }

    private void save(Player player) {
        Profile profile = API.getProfile(player);
        if (profile != null) Profile.save(profile);
    }
}
