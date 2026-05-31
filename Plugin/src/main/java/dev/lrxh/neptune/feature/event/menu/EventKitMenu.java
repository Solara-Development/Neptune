package dev.lrxh.neptune.feature.event.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.event.EventType;
import dev.lrxh.neptune.feature.event.menu.button.EventKitButton;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EventKitMenu extends Menu {
    private final EventType eventType;

    public EventKitMenu(EventType eventType) {
        super(MenusLocale.EVENT_KIT_SELECT_TITLE.getString(),
                MenusLocale.EVENT_KIT_SELECT_SIZE.getInt(),
                Filter.FILL);
        this.eventType = eventType;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (Kit kit : KitService.get().kits) {
            if (kit.is(KitRule.HIDDEN)) continue;
            buttons.add(new EventKitButton(kit.getSlot(), eventType, kit));
        }
        buttons.add(new Button(getSize() - 1) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(MenusLocale.EVENT_KIT_CUSTOM_MATERIAL.getString())
                        .name(MenusLocale.EVENT_KIT_CUSTOM_NAME.getString())
                        .lore(MenusLocale.EVENT_KIT_CUSTOM_LORE.getStringList())
                        .build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                new EventCustomKitMenu(eventType).open(p);
            }
        });
        return buttons;
    }
}
