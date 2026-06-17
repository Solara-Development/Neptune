package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.KitsManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitDeleteButton extends Button {
    private final Kit kit;

    public KitDeleteButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        kit.delete();
        new KitsManagementMenu().open(player);
        player.sendMessage(CC.success("Deleted Kit"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.RED_DYE)
                .name("&cDelete kit")
                .lore("&7Click to permanently remove this kit")
                .build();
    }
}
