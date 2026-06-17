package dev.lrxh.neptune.game.duel.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.feature.customkit.CustomKitService;
import dev.lrxh.neptune.game.duel.DuelRequest;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomKitSelectMenu extends Menu {
    private final Player receiver;
    private final boolean party;

    public CustomKitSelectMenu(Player receiver, boolean party) {
        super(MenusLocale.DUEL_CUSTOM_KIT_TITLE.getString().replaceAll("<target>", receiver.getName()), MenusLocale.CUSTOM_KIT_SIZE.getInt(), Filter.valueOf(MenusLocale.CUSTOM_KIT_FILTER.getString()));
        this.receiver = receiver;
        this.party = party;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int slot = 0;
        for (CustomKit kit : CustomKitService.get().get(player.getUniqueId())) {
            if (kit.getArenaNames().isEmpty()) continue;
            buttons.add(new Button(slot++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(kit.getIcon())
                            .name(MenusLocale.CUSTOM_KIT_ITEM_NAME.getString().replace("<kit>", kit.getDisplayName()))
                            .lore(MenusLocale.DUEL_CUSTOM_KIT_ITEM_LORE.getStringList()).build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    Profile profile = API.getProfile(receiver);
                    if (profile == null) return;
                    Kit transientKit = kit.toTransientKit();
                    transientKit.getRandomArena().thenAccept(arena -> {
                        if (arena == null) {
                            p.sendMessage(CC.error("No arena found, please contact an admin"));
                            return;
                        }
                        DuelRequest duelRequest = new DuelRequest(p.getUniqueId(), transientKit, arena, party, transientKit.getRounds());
                        profile.sendRequest(duelRequest, false);
                        Bukkit.getScheduler().runTask(Neptune.get(), () -> p.closeInventory());
                    });
                }
            });
        }
        return buttons;
    }
}
