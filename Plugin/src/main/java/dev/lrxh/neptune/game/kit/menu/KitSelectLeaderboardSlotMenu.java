package dev.lrxh.neptune.game.kit.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.button.KitSelectLeaderboardSlotButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitSelectLeaderboardSlotMenu extends Menu {
    private final Kit kit;

    public KitSelectLeaderboardSlotMenu(Kit kit) {
        super("&eSelect Leaderboard Slot", MenusLocale.LEADERBOARD_SIZE.getInt(), Filter.NONE);
        this.kit = kit;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (int i = 0; i < MenusLocale.LEADERBOARD_SIZE.getInt(); i++) {
            buttons.add(new KitSelectLeaderboardSlotButton(i, kit));
        }

        return buttons;
    }
}
