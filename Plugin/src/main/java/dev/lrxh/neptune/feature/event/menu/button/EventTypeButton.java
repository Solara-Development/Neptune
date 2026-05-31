package dev.lrxh.neptune.feature.event.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.event.EventType;
import dev.lrxh.neptune.feature.event.menu.EventKitMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class EventTypeButton extends Button {
    private final EventType type;

    public EventTypeButton(EventType type) {
        super(getSlot(type));
        this.type = type;
    }

    private static int getSlot(EventType type) {
        return switch (type) {
            case TOURNAMENT -> MenusLocale.EVENT_TYPE_TOURNAMENT_SLOT.getInt();
            case LMS -> MenusLocale.EVENT_TYPE_LMS_SLOT.getInt();
        };
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return switch (type) {
            case TOURNAMENT -> new ItemBuilder(MenusLocale.EVENT_TYPE_TOURNAMENT_MATERIAL.getString())
                    .name(MenusLocale.EVENT_TYPE_TOURNAMENT_NAME.getString())
                    .lore(MenusLocale.EVENT_TYPE_TOURNAMENT_LORE.getStringList(), player).build();
            case LMS -> new ItemBuilder(MenusLocale.EVENT_TYPE_LMS_MATERIAL.getString())
                    .name(MenusLocale.EVENT_TYPE_LMS_NAME.getString())
                    .lore(MenusLocale.EVENT_TYPE_LMS_LORE.getStringList(), player).build();
        };
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new EventKitMenu(this.type).open(player);
    }
}
