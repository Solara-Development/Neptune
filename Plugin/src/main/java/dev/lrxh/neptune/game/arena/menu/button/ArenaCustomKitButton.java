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

public class ArenaCustomKitButton extends Button {
    private final Arena arena;

    public ArenaCustomKitButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        arena.setAllowedInCustomKit(!arena.isAllowedInCustomKit());
        ArenaService.get().save();
        new ArenaManagementMenu(arena).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        if (arena.isAllowedInCustomKit()) {
            return new ItemBuilder(Material.GREEN_WOOL).name("&aAllowed in Custom Kits")
                    .lore("&7Click to disallow custom kits in this arena")
                    .build();
        }
        return new ItemBuilder(Material.RED_WOOL).name("&cNot Allowed in Custom Kits")
                .lore("&7Click to allow custom kits in this arena")
                .build();
    }
}
