package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.menu.ArenaManagementMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaEnableButton extends Button {
    private final Arena arena;

    public ArenaEnableButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        arena.setEnabled(!arena.isEnabled());
        ArenaService.get().save();
        new ArenaManagementMenu(arena).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        if (arena.isEnabled()) return new ItemBuilder(Material.GREEN_WOOL).name("&cClick to disable").build();
        return new ItemBuilder(Material.RED_WOOL).name("&aClick to enable").build();
    }
}
