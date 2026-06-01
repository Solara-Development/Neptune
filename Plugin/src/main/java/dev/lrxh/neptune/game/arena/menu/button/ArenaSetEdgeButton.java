package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.impl.EdgeType;
import dev.lrxh.neptune.game.arena.procedure.ArenaProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


public class ArenaSetEdgeButton extends Button {
    private final Arena arena;
    private final EdgeType edgeType;

    public ArenaSetEdgeButton(int slot, Arena arena, EdgeType edgeType) {
        super(slot, false);
        this.arena = arena;
        this.edgeType = edgeType;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getArenaProcedure().setArena(arena);

        if (edgeType.equals(EdgeType.MIN)) {
            profile.getArenaProcedure().setType(ArenaProcedureType.SET_SPAWN_MIN);
            player.sendMessage(CC.info("Go to the lowest edge of the arena and type &aDone"));
        } else {
            profile.getArenaProcedure().setType(ArenaProcedureType.SET_SPAWN_MAX);
            player.sendMessage(CC.info("Go to the highest edge of the arena and type &aDone"));
        }
        player.closeInventory();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        if (edgeType.equals(EdgeType.MIN)) {
            return new ItemBuilder(Material.BLUE_DYE).name("&9Set lowest edge")
                    .lore("&7Click to set the lowest corner of the arena boundary",
                            "&7Min: " + arena.getMin().getBlockX() + " Y: " + arena.getMin().getBlockY() + " Z: " + arena.getMin().getBlockZ())
                    .build();
        }
        return new ItemBuilder(Material.RED_DYE).name("&cSet highest edge")
                .lore("&7Click to set the highest corner of the arena boundary",
                        "&7X: " + arena.getMax().getBlockX() + " Y: " + arena.getMax().getBlockY() + " Z: " + arena.getMax().getBlockZ())
                .build();
    }
}
