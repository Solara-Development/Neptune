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

public class ArenaSetupButton extends Button {
    private final Arena arena;

    public ArenaSetupButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                .name("&eSetup arena")
                .lore("&7Click to begin setting up the arena's spawns, edges, and boundaries")
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getArenaProcedure().setType(ArenaProcedureType.SET_SPAWN_RED);
        profile.getArenaProcedure().setArena(arena);
        player.closeInventory();
        if (arena.getRedSpawn() != null) player.teleport(arena.getRedSpawn());
        player.sendMessage(CC.info("Go to the spawn of the &cred&7 player and type &aDone"));
    }
}
