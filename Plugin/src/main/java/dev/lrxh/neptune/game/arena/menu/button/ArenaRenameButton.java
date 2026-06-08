package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.menu.ArenaManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaRenameButton extends Button {
    private final Arena arena;

    public ArenaRenameButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.NAME_TAG)
                .name("&eRename arena &7(" + arena.getDisplayName() + "&7)")
                .lore("&7Click to change the display name of this arena")
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.closeInventory();
        SignInputMenu.open(player, arena.getDisplayName(), "Enter display name", input -> {
            arena.setDisplayName(input);
            player.sendMessage(CC.success("Renamed arena"));
            new ArenaManagementMenu(arena).open(player);
            ArenaService.get().save();
        });
    }
}
