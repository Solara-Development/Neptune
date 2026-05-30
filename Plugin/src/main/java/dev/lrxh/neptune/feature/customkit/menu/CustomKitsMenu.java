package dev.lrxh.neptune.feature.customkit.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.feature.customkit.CustomKitService;
import dev.lrxh.neptune.feature.customkit.queue.CustomKitQueueService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomKitsMenu extends Menu {
    public static final String PERMISSION = "neptune.customkit";

    private final boolean hostMode;

    public CustomKitsMenu() {
        this(false);
    }

    public CustomKitsMenu(boolean hostMode) {
        super(hostMode ? MenusLocale.CUSTOM_KIT_HOST_TITLE.getString() : MenusLocale.CUSTOM_KIT_TITLE.getString(),
                MenusLocale.CUSTOM_KIT_SIZE.getInt(), Filter.valueOf(MenusLocale.CUSTOM_KIT_FILTER.getString()));
        this.hostMode = hostMode;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<CustomKit> kits = CustomKitService.get().get(player.getUniqueId());

        int slot = 0;
        for (CustomKit kit : kits) {
            buttons.add(new Button(slot++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(kit.getIcon())
                            .name(MenusLocale.CUSTOM_KIT_ITEM_NAME.getString().replace("<kit>", kit.getDisplayName()))
                            .lore(hostMode ? MenusLocale.CUSTOM_KIT_ITEM_HOST_LORE.getStringList()
                                    : MenusLocale.CUSTOM_KIT_ITEM_MANAGE_LORE.getStringList()).build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    if (hostMode) {
                        CustomKitQueueService.get().host(p, kit);
                    } else {
                        new CustomKitManageMenu(kit).open(p);
                    }
                }
            });
        }

        if (!hostMode) {
            buttons.add(new Button(MenusLocale.CUSTOM_KIT_CREATE_SLOT.getInt()) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(MenusLocale.CUSTOM_KIT_CREATE_MATERIAL.getString())
                            .name(MenusLocale.CUSTOM_KIT_CREATE_NAME.getString())
                            .lore(MenusLocale.CUSTOM_KIT_CREATE_LORE.getStringList(), TagResolver.resolver(
                                    Placeholder.unparsed("kits", String.valueOf(kits.size())),
                                    Placeholder.unparsed("max", String.valueOf(CustomKitService.MAX_KITS))), p)
                            .build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    if (!p.hasPermission(PERMISSION)) {
                        MessagesLocale.CUSTOM_KIT_NO_PERMISSION.send(p.getUniqueId());
                        return;
                    }
                    if (kits.size() >= CustomKitService.MAX_KITS) {
                        MessagesLocale.CUSTOM_KIT_MAX.send(p.getUniqueId());
                        return;
                    }
                    p.closeInventory();
                    SignInputMenu.open(p, "", "Enter kit name", input -> {
                        CustomKit created = CustomKitService.get().create(p.getUniqueId(), input);
                        if (created == null) {
                            MessagesLocale.CUSTOM_KIT_CREATE_FAIL.send(p.getUniqueId());
                            return;
                        }
                        Profile profile = API.getProfile(p);
                        if (profile != null) Profile.save(profile);
                        MessagesLocale.CUSTOM_KIT_CREATED.send(p.getUniqueId(),
                                Placeholder.parsed("kit", created.getDisplayName()));
                        new CustomKitManageMenu(created).open(p);
                    });
                }
            });
        }

        return buttons;
    }
}
