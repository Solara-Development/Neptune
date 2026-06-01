package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.procedure.ArenaProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


public class ArenaSetLimitButton extends Button {
    private final Arena arena;

    public ArenaSetLimitButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.LADDER)
                .name("&cSet Build Limit")
                .lore("&7Click to set the maximum build height for this arena")
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getArenaProcedure().setArena(arena);
        profile.getArenaProcedure().setType(ArenaProcedureType.SET_BUILD_LIMIT);
        player.sendMessage(CC.info("Go to the build limit and type &aDone"));

        player.closeInventory();
    }
}