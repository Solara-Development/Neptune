package dev.lrxh.neptune.feature.queue.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.customkit.queue.CustomKitListingsMenu;
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

public class QueueMenu extends Menu {

    public QueueMenu() {
        super(MenusLocale.QUEUE_SELECT_TITLE.getString(), MenusLocale.QUEUE_SELECT_SIZE.getInt(), Filter.valueOf(MenusLocale.QUEUE_SELECT_FILTER.getString()));
        if (SettingsLocale.QUEUE_MENU_LIVE_UPDATE.getBoolean()) {
            setUpdateEveryTick(true);
            setUpdateInterval(20L);
        }
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (Kit kit : KitService.get().kits) {
            if (kit.getRules().get(KitRule.HIDDEN)) continue;
            buttons.add(new QueueSelectButton(kit.getSlot(), kit));
        }
        buttons.add(new Button(MenusLocale.CUSTOM_KIT_QUEUE_BUTTON_SLOT.getInt()) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(MenusLocale.CUSTOM_KIT_QUEUE_BUTTON_MATERIAL.getString())
                        .name(MenusLocale.CUSTOM_KIT_QUEUE_BUTTON_NAME.getString())
                        .lore(MenusLocale.CUSTOM_KIT_QUEUE_BUTTON_LORE.getStringList()).build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                new CustomKitListingsMenu().open(p);
            }
        });
        return buttons;
    }
}
