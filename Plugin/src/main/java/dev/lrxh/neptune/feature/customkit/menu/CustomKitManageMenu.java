package dev.lrxh.neptune.feature.customkit.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.feature.customkit.CustomKitService;
import dev.lrxh.neptune.feature.itembrowser.ItemBrowserService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CustomKitManageMenu extends Menu {
    private final CustomKit kit;

    public CustomKitManageMenu(CustomKit kit) {
        super(MenusLocale.CUSTOM_KIT_MANAGE_TITLE.getString().replace("<kit>", kit.getDisplayName()),
                MenusLocale.CUSTOM_KIT_MANAGE_SIZE.getInt(),
                Filter.valueOf(MenusLocale.CUSTOM_KIT_MANAGE_FILTER.getString()));
        this.kit = kit;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(action(MenusLocale.CUSTOM_KIT_MANAGE_EDIT_ITEMS_SLOT.getInt(),
                new ItemBuilder(MenusLocale.CUSTOM_KIT_MANAGE_EDIT_ITEMS_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_MANAGE_EDIT_ITEMS_NAME.getString()).build(),
                p -> new CustomKitEditorMenu(kit).open(p)));

        buttons.add(action(MenusLocale.CUSTOM_KIT_MANAGE_RULES_SLOT.getInt(),
                new ItemBuilder(MenusLocale.CUSTOM_KIT_MANAGE_RULES_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_MANAGE_RULES_NAME.getString()).build(),
                p -> new CustomKitRulesMenu(kit).open(p)));

        buttons.add(action(MenusLocale.CUSTOM_KIT_MANAGE_ARENAS_SLOT.getInt(),
                new ItemBuilder(MenusLocale.CUSTOM_KIT_MANAGE_ARENAS_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_MANAGE_ARENAS_NAME.getString()).build(),
                p -> new CustomKitArenaMenu(kit).open(p)));

        buttons.add(action(MenusLocale.CUSTOM_KIT_MANAGE_WHITELIST_SLOT.getInt(),
                new ItemBuilder(MenusLocale.CUSTOM_KIT_MANAGE_WHITELIST_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_MANAGE_WHITELIST_NAME.getString()).build(),
                p -> new CustomKitWhitelistMenu(kit).open(p)));

        buttons.add(action(MenusLocale.CUSTOM_KIT_MANAGE_EFFECTS_SLOT.getInt(),
                new ItemBuilder(MenusLocale.CUSTOM_KIT_MANAGE_EFFECTS_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_MANAGE_EFFECTS_NAME.getString()).build(),
                p -> new CustomKitEffectsMenu(kit).open(p)));

        buttons.add(action(MenusLocale.CUSTOM_KIT_MANAGE_ICON_SLOT.getInt(),
                new ItemBuilder(MenusLocale.CUSTOM_KIT_MANAGE_ICON_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_MANAGE_ICON_NAME.getString()).build(),
                p -> ItemBrowserService.get().openBrowser(p, "items", material -> {
                    kit.setIcon(new ItemStack(material));
                    save(p);
                    new CustomKitManageMenu(kit).open(p);
                }, () -> new CustomKitManageMenu(kit).open(p))));

        buttons.add(new Button(MenusLocale.CUSTOM_KIT_MANAGE_HEALTH_SLOT.getInt()) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(MenusLocale.CUSTOM_KIT_MANAGE_HEALTH_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_MANAGE_HEALTH_NAME.getString())
                        .lore(MenusLocale.CUSTOM_KIT_MANAGE_HEALTH_LORE.getStringList(),
                                Placeholder.unparsed("health", String.valueOf(kit.getHealth())), p).build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                CustomKitService.get().await(p.getUniqueId(), CustomKitService.Input.HEALTH, kit);
                p.closeInventory();
                MessagesLocale.CUSTOM_KIT_HEALTH_PROMPT.send(p.getUniqueId());
            }
        });

        buttons.add(new Button(MenusLocale.CUSTOM_KIT_MANAGE_DELETE_SLOT.getInt()) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(MenusLocale.CUSTOM_KIT_MANAGE_DELETE_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_MANAGE_DELETE_NAME.getString())
                        .lore(MenusLocale.CUSTOM_KIT_MANAGE_DELETE_LORE.getStringList()).build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                if (!type.isShiftClick()) {
                    MessagesLocale.CUSTOM_KIT_DELETE_CONFIRM.send(p.getUniqueId());
                    return;
                }
                CustomKitService.get().delete(p.getUniqueId(), kit);
                save(p);
                MessagesLocale.CUSTOM_KIT_DELETED.send(p.getUniqueId());
                new CustomKitsMenu().open(p);
            }
        });

        buttons.add(new ReturnButton(MenusLocale.CUSTOM_KIT_MANAGE_RETURN_SLOT.getInt(), new CustomKitsMenu()));
        return buttons;
    }

    private Button action(int slot, ItemStack item, Consumer<Player> onClick) {
        return new Button(slot) {
            @Override
            public ItemStack getItemStack(Player p) {
                return item;
            }

            @Override
            public void onClick(ClickType type, Player p) {
                onClick.accept(p);
            }
        };
    }

    private void save(Player player) {
        Profile profile = API.getProfile(player);
        if (profile != null) Profile.save(profile);
    }
}
