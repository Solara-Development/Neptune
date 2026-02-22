package dev.lrxh.neptune.game.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RoundsSelectMenu extends Menu {
    private final Kit kit;
    private final Player receiver;

    public RoundsSelectMenu(Kit kit, Player receiver) {
        super(MenusLocale.ROUNDS_TITLE.getString().replaceAll("<target>", receiver.getName()), MenusLocale.ROUNDS_SIZE.getInt(), Filter.valueOf(MenusLocale.ROUNDS_FILTER.getString()));
        this.kit = kit;
        this.receiver = receiver;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = MenusLocale.ROUNDS_STARTING_SLOT.getInt();

        String[] parts = MenusLocale.ROUNDS_LIST.getString().replace(" ", "").split(",");

        for (String round : parts) {
            buttons.add(new RoundSelectButton(i++, kit, receiver, Integer.parseInt(round)));
        }

        return buttons;
    }
}
