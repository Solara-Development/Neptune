package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.procedure.ArenaProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;


public class ArenaSetDeathYButton extends Button {
    private final Arena arena;

    public ArenaSetDeathYButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.GOLD_INGOT)
                .name("&cSet Arena Death Y")
                .lore("&7Click to set the Y level where players die from falling out of the arena")
                .build();
    }


    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getArenaProcedure().setArena(arena);
        profile.getArenaProcedure().setType(ArenaProcedureType.SET_DEATH_Y);
        player.sendMessage(CC.info("Go to lowest point of the arena and type &aDone"));

        player.closeInventory();
    }
}
