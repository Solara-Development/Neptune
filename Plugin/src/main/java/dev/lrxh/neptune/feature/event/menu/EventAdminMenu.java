package dev.lrxh.neptune.feature.event.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.event.EventType;
import dev.lrxh.neptune.feature.event.menu.button.EventTypeButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EventAdminMenu extends Menu {

    public EventAdminMenu() {
        super(MenusLocale.EVENT_ADMIN_TITLE.getString(),
                MenusLocale.EVENT_ADMIN_SIZE.getInt(),
                Filter.valueOf(MenusLocale.EVENT_ADMIN_FILTER.getString()));
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (EventType type : EventType.values()) {
            buttons.add(new EventTypeButton(type));
        }
        return buttons;
    }
}
