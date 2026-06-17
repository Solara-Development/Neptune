package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.KitRulesMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitRulesButton extends Button {
    private final Kit kit;

    public KitRulesButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new KitRulesMenu(kit).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.COMPARATOR)
                .name("&bView Rules")
                .lore("&7Click to view and configure this kit's rules")
                .build();
    }
}
