package dev.lrxh.neptune.feature.customkit.queue;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.feature.customkit.CustomKitService;
import dev.lrxh.neptune.feature.customkit.menu.CustomKitsMenu;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomKitListingsMenu extends PaginatedMenu {

    public CustomKitListingsMenu() {
        super(MenusLocale.CUSTOM_KIT_LISTINGS_TITLE.getString(), MenusLocale.CUSTOM_KIT_LISTINGS_SIZE.getInt(), Filter.NONE);
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (Map.Entry<UUID, CustomKit> entry : CustomKitQueueService.get().getListings().entrySet()) {
            UUID hostUUID = entry.getKey();
            CustomKit kit = entry.getValue();
            Player host = Bukkit.getPlayer(hostUUID);
            if (host == null) continue;
            buttons.add(new Button(i++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(kit.getIcon())
                            .name(MenusLocale.CUSTOM_KIT_LISTINGS_NAME.getString()
                                    .replace("<player>", host.getName()).replace("<kit>", kit.getDisplayName()))
                            .lore(MenusLocale.CUSTOM_KIT_LISTINGS_LORE.getStringList()).build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    CustomKitQueueService.get().join(p, hostUUID);
                }
            });
        }
        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new Button(MenusLocale.CUSTOM_KIT_JOIN_QUEUE_SLOT.getInt()) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(MenusLocale.CUSTOM_KIT_JOIN_QUEUE_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_JOIN_QUEUE_NAME.getString())
                        .lore(MenusLocale.CUSTOM_KIT_JOIN_QUEUE_LORE.getStringList()).build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                Profile profile = API.getProfile(p);
                if (profile == null || !profile.hasState(ProfileState.IN_LOBBY)) {
                    MessagesLocale.CUSTOM_KIT_CANT_HOST.send(p.getUniqueId());
                    return;
                }
                if (CustomKitService.get().get(p.getUniqueId()).isEmpty()) {
                    MessagesLocale.CUSTOM_KIT_NO_KITS.send(p.getUniqueId());
                    return;
                }
                new CustomKitsMenu(true).open(p);
            }
        });
        return buttons;
    }

    @Override
    public int getMaxItemsPerPage() {
        return 36;
    }
}
