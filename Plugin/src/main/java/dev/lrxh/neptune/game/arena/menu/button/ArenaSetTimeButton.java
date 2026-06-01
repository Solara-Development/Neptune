package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.menu.ArenaManagementMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;


public class ArenaSetTimeButton extends Button {
    private final Arena arena;

    public ArenaSetTimeButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.GOLD_INGOT)
                .name("&cSet Arena Time")
                .lore("&7Click to change the time of day in this arena")
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.closeInventory();
        SignInputMenu.open(player, "", "Time (ticks/day/night/current)", input -> {
            if (input.equalsIgnoreCase("day")) arena.setTime(1000);
            if (input.equalsIgnoreCase("night")) arena.setTime(13000);
            else if (input.equalsIgnoreCase("noon")) arena.setTime(6000);
            else if (input.equalsIgnoreCase("midnight")) arena.setTime(18000);
            else if (input.equalsIgnoreCase("sunrise")) arena.setTime(23000);
            else if (input.equalsIgnoreCase("sunset")) arena.setTime(12000);
            else if (input.equalsIgnoreCase("current")) arena.setTime(player.getWorld().getTime());
            else {
                try {
                    arena.setTime(Long.parseLong(input));
                } catch (NumberFormatException e) {
                    player.sendMessage(CC.error("Invalid time input"));
                    return;
                }
            }
            player.sendMessage(CC.success("Set arena time"));
            new ArenaManagementMenu(arena).open(player);
            ArenaService.get().save();
        });
    }
}
