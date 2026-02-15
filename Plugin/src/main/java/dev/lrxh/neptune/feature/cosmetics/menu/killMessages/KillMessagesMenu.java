package dev.lrxh.neptune.feature.cosmetics.menu.killMessages;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.cosmetics.CosmeticService;
import dev.lrxh.neptune.feature.cosmetics.impl.killmessage.KillMessagePackage;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KillMessagesMenu extends Menu {
    public KillMessagesMenu() {
        super(MenusLocale.KILL_MESSAGES_TITLE.getString(), MenusLocale.KILL_MESSAGES_SIZE.getInt(), Filter.valueOf(MenusLocale.KILL_MESSAGES_FILTER.getString()), true);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (KillMessagePackage killMessagePackage : CosmeticService.get().getKillMessagePackages().values()) {
            buttons.add(new KillMessageButton(killMessagePackage.getSlot(), killMessagePackage));
        }

        return buttons;
    }
}
