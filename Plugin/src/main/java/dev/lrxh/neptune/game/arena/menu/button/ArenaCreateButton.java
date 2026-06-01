package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.menu.ArenasManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaCreateButton extends Button {

    public ArenaCreateButton(int slot) {
        super(slot, false);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                .name("&aCreate arena")
                .lore("&7Click to create a new arena")
                .build();
    }


    @Override
    public void onClick(ClickType type, Player player) {
        Location location = Neptune.get().getCache().getSpawn();
        if (location == null) {
            player.sendMessage(CC.error("Spawn world is not set!, use /neptune setspawn"));
            return;
        }

        player.closeInventory();
        SignInputMenu.open(player, "", "Enter arena name", input -> {
            if (ArenaService.get().getArenaByName(input) != null) {
                player.sendMessage(CC.error("Arena already exists"));
                return;
            }
            if (input.contains(" ")) {
                player.sendMessage(CC.error("Arena name cannot contain spaces"));
                return;
            }
            ArenaService.get().arenas.add(new Arena(input, player.getWorld().getTime()));
            player.sendMessage(CC.success("Created arena"));
            new ArenasManagementMenu().open(player);
            ArenaService.get().save();
        });
    }
}
