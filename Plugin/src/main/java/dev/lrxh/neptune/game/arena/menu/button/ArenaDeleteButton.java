package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.menu.ArenasManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaDeleteButton extends Button {
    private final Arena arena;

    public ArenaDeleteButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.RED_DYE)
                .name("&cDelete arena")
                .lore("&7Click to permanently remove this arena")
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        arena.delete(true);
        new ArenasManagementMenu().open(player);
        player.sendMessage(CC.success("Deleted arena"));
    }
}
