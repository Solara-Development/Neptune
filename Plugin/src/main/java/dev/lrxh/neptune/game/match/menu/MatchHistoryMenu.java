package dev.lrxh.neptune.game.match.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchHistoryMenu extends Menu {
    private final Player target;

    public MatchHistoryMenu(Player target) {
        super(MenusLocale.MATCH_HISTORY_TITLE.getString(), MenusLocale.MATCH_HISTORY_SIZE.getInt(),
                Filter.valueOf(MenusLocale.MATCH_HISTORY_FILTER.getString()));
        this.target = target;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        Profile profile = API.getProfile(target);

        int i = MenusLocale.MATCH_HISTORY_STARTING_SLOT.getInt();

        ArrayList<MatchHistory> matchHistories = new ArrayList<>(profile.getGameData().getMatchHistories());
        Collections.reverse(matchHistories);

        for (MatchHistory matchHistory : matchHistories) {
            buttons.add(new DisplayButton(i++, getButtonItem(target, matchHistory)));
        }

        return buttons;
    }

    public ItemStack getButtonItem(Player player, MatchHistory matchHistory) {
        Kit kit = KitService.get().getKitByDisplay(matchHistory.getKitName());
        if (kit == null) {
            return new ItemBuilder(Material.COMPASS)
                    .name(MenusLocale.MATCH_HISTORY_ITEM_NAME.getString()
                            .replace("<kit>", matchHistory.getKitName())
                            .replace("<won>",
                                    matchHistory.isWon()
                                            ? MenusLocale.MATCH_HISTORY_WON
                                            .getString()
                                            : MenusLocale.MATCH_HISTORY_LOST
                                            .getString())
                            .replace("<winner>",
                                    matchHistory.isWon() ? player.getName()
                                            : matchHistory.getOpponentName())
                            .replace("<loser>",
                                    !matchHistory.isWon() ? player.getName()
                                            : matchHistory.getOpponentName()))
                    .lore(ItemUtils.getLore(MenusLocale.MATCH_HISTORY_LORE.getStringList(),
                            new Replacement("<arena>", matchHistory.getArenaName()),
                            new Replacement("<kit>", matchHistory.getKitName()),
                            new Replacement("<winner>",
                                    matchHistory.isWon() ? player.getName()
                                            : matchHistory.getOpponentName()),
                            new Replacement("<loser>",
                                    !matchHistory.isWon() ? player.getName()
                                            : matchHistory.getOpponentName()),
                            new Replacement("<date>", matchHistory.getDate())), player)

                    .build();
        } else {
            return new ItemBuilder(kit.getIcon())
                    .name(MenusLocale.MATCH_HISTORY_ITEM_NAME.getString()
                            .replace("<kit>", matchHistory.getKitName())
                            .replace("<won>",
                                    matchHistory.isWon()
                                            ? MenusLocale.MATCH_HISTORY_WON
                                            .getString()
                                            : MenusLocale.MATCH_HISTORY_LOST
                                            .getString())
                            .replace("<winner>",
                                    matchHistory.isWon() ? player.getName()
                                            : matchHistory.getOpponentName())
                            .replace("<loser>",
                                    !matchHistory.isWon() ? player.getName()
                                            : matchHistory.getOpponentName()))
                    .lore(ItemUtils.getLore(MenusLocale.MATCH_HISTORY_LORE.getStringList(),
                            new Replacement("<arena>", matchHistory.getArenaName()),
                            new Replacement("<kit>", matchHistory.getKitName()),
                            new Replacement("<winner>",
                                    matchHistory.isWon() ? player.getName()
                                            : matchHistory.getOpponentName()),
                            new Replacement("<loser>",
                                    !matchHistory.isWon() ? player.getName()
                                            : matchHistory.getOpponentName()),
                            new Replacement("<date>", matchHistory.getDate())), player)

                    .build();
        }

    }
}
