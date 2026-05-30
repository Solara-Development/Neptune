package dev.lrxh.neptune.feature.customkit.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.feature.itembrowser.ItemBrowserService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomKitWhitelistMenu extends PaginatedMenu {
    private final CustomKit kit;

    public CustomKitWhitelistMenu(CustomKit kit) {
        super(MenusLocale.CUSTOM_KIT_WHITELIST_TITLE.getString(), MenusLocale.CUSTOM_KIT_WHITELIST_SIZE.getInt(), Filter.NONE);
        this.kit = kit;
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (Material material : new ArrayList<>(kit.getWhitelistedBlocks())) {
            buttons.add(new Button(i++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(material)
                            .name(MenusLocale.CUSTOM_KIT_WHITELIST_ITEM_NAME.getString().replace("<block>", material.name()))
                            .lore(MenusLocale.CUSTOM_KIT_WHITELIST_REMOVE_LORE.getStringList()).build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    kit.getWhitelistedBlocks().remove(material);
                    save(p);
                    open(p);
                }
            });
        }
        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new ReturnButton(getSize() - 9, new CustomKitManageMenu(kit)));
        buttons.add(new Button(getSize() - 5) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(MenusLocale.CUSTOM_KIT_WHITELIST_ADD_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_WHITELIST_ADD_NAME.getString()).build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                ItemBrowserService.get().openBrowser(p, "blocks", material -> {
                    if (!kit.getWhitelistedBlocks().contains(material)) kit.getWhitelistedBlocks().add(material);
                    save(p);
                    new CustomKitWhitelistMenu(kit).open(p);
                }, () -> new CustomKitWhitelistMenu(kit).open(p));
            }
        });
        return buttons;
    }

    @Override
    public int getMaxItemsPerPage() {
        return 36;
    }

    private void save(Player player) {
        Profile profile = API.getProfile(player);
        if (profile != null) Profile.save(profile);
    }
}
