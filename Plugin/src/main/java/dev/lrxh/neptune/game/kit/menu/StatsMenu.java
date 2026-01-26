package dev.lrxh.neptune.game.kit.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.kit.menu.button.StatButton;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StatsMenu extends Menu {
    private final Player target;

    public StatsMenu(String name) {
        super(MenusLocale.STAT_SIZE.getInt(), Filter.valueOf((MenusLocale.STAT_FILTER.getString())));
        this.target = Bukkit.getPlayer(name);
    }

    public StatsMenu(Player player) {
        super(MenusLocale.STAT_SIZE.getInt(), Filter.valueOf((MenusLocale.STAT_FILTER.getString())));
        this.target = player;
    }

    @Override
    public String getTitle(Player player) {
        return PlaceholderUtil.format(MenusLocale.STAT_TITLE.getString(), player);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (Kit kit : KitService.get().kits) {
            if (kit.getRules().get(KitRule.HIDDEN)) continue;
            buttons.add(new StatButton(kit.getSlot(), kit, target));
        }
        return buttons;
    }
}