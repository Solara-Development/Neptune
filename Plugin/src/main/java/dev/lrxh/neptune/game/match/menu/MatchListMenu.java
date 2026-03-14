package dev.lrxh.neptune.game.match.menu;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.menu.button.MatchSpectateButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MatchListMenu extends PaginatedMenu {
    private final int size = MenusLocale.MATCH_LIST_SIZE.getInt();
    public MatchListMenu() {
        super(MenusLocale.MATCH_LIST_TITLE.getString(), MenusLocale.MATCH_LIST_SIZE.getInt(),
                Filter.valueOf(MenusLocale.MATCH_LIST_FILTER.getString()));
    }
    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = MenusLocale.MATCH_LIST_STARTING_SLOT.getInt();
        int rows = size / 9;
        boolean isBorder = MenusLocale.MATCH_LIST_FILTER.getString().equals("BORDER");
        for (Match match : MatchService.get().matches) {
            if (match instanceof SoloFightMatch SoloFightMatch) {
                int row = i / 9;
                int col = i % 9;
                if (col == 8 && isBorder) i += 2;
                if (row == rows - 1 && isBorder) i += 9;
                buttons.add(new MatchSpectateButton(i++, SoloFightMatch));
            }
        }

        return buttons;
    }
}
