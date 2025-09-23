package dev.lrxh.neptune.game.match.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.menu.button.MatchSpectateButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MatchListMenu extends Menu {

    public MatchListMenu() {
        super(MenusLocale.MATCH_LIST_TITLE.getString(), MenusLocale.MATCH_LIST_SIZE.getInt(),
                Filter.valueOf(MenusLocale.MATCH_LIST_FILTER.getString()));
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = MenusLocale.MATCH_LIST_STARTING_SLOT.getInt();

        for (Match match : MatchService.get().matches) {
            if (match instanceof SoloFightMatch SoloFightMatch) {
                buttons.add(new MatchSpectateButton(i++, SoloFightMatch));
            }
        }

        return buttons;
    }
}
