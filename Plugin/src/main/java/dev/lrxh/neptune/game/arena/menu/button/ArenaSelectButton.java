package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.menu.ArenaManagementMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaSelectButton extends Button {
    private final Arena arena;

    public ArenaSelectButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new ArenaManagementMenu(arena).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.DIAMOND_SWORD).name("&f" + arena.getName() + " &7(" + arena.getDisplayName() + "&7)")
                .lore("&7Click to open this arena's management settings",
                        "&7Enabled: " + (arena.isEnabled() ? "&aEnabled" : "&cDisabled"),
                        "&7Setup: " + (arena.isSetup() ? "&aDone" : "&cNot Done"),
                        "&7Snapshot: " + (arena.isDoneLoading() ? "&aDone" : arena.isSetup() ? "&eLoading..." : "&cNot Setup"))
                .build();
    }
}
