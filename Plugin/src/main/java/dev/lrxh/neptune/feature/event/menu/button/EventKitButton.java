package dev.lrxh.neptune.feature.event.menu.button;

import dev.lrxh.neptune.feature.event.EventService;
import dev.lrxh.neptune.feature.event.EventType;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class EventKitButton extends Button {
    private final EventType eventType;
    private final Kit kit;

    public EventKitButton(int slot, EventType eventType, Kit kit) {
        super(slot);
        this.eventType = eventType;
        this.kit = kit;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(kit.getIcon())
                .name(kit.getDisplayName())
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.closeInventory();
        EventService.get().startEvent(eventType, kit, player.getUniqueId());
    }
}
