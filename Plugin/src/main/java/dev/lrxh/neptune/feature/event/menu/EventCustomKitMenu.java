package dev.lrxh.neptune.feature.event.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.feature.customkit.CustomKitService;
import dev.lrxh.neptune.feature.event.EventService;
import dev.lrxh.neptune.feature.event.EventType;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EventCustomKitMenu extends Menu {
    private final EventType eventType;

    public EventCustomKitMenu(EventType eventType) {
        super(MenusLocale.EVENT_KIT_SELECT_TITLE.getString(),
                MenusLocale.EVENT_KIT_SELECT_SIZE.getInt(),
                Filter.FILL);
        this.eventType = eventType;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<CustomKit> kits = CustomKitService.get().get(player.getUniqueId());
        for (int i = 0; i < kits.size(); i++) {
            final CustomKit kit = kits.get(i);
            final int slot = i;
            buttons.add(new Button(slot) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(kit.getIcon())
                            .name(kit.getDisplayName())
                            .build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    p.closeInventory();
                    EventService.get().startEvent(eventType, kit.toTransientKit(), p.getUniqueId());
                }
            });
        }
        return buttons;
    }
}
