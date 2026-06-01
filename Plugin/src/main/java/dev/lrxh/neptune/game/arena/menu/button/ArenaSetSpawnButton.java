package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.procedure.ArenaProcedureType;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


public class ArenaSetSpawnButton extends Button {
    private final Arena arena;
    private final ParticipantColor participantColor;

    public ArenaSetSpawnButton(int slot, Arena arena, ParticipantColor participantColor) {
        super(slot, false);
        this.arena = arena;
        this.participantColor = participantColor;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getArenaProcedure().setArena(arena);

        if (participantColor.equals(ParticipantColor.BLUE)) {
            profile.getArenaProcedure().setType(ArenaProcedureType.SET_SPAWN_BLUE);
            player.sendMessage(CC.info("Go to the spawn of the &9blue &7player and type &aDone"));
        } else {
            profile.getArenaProcedure().setType(ArenaProcedureType.SET_SPAWN_RED);
            player.sendMessage(CC.info("Go to the spawn of the &cred&7 player and type &aDone"));
        }
        player.closeInventory();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        if (participantColor.equals(ParticipantColor.RED)) {
            return new ItemBuilder(Material.RED_WOOL).name("&cSet Red spawn")
                    .lore("&7Click to set the red team's spawn point",
                            "&7X: " + arena.getRedSpawn().getBlockX() + " Y: " + arena.getRedSpawn().getBlockY() + " Z: " + arena.getRedSpawn().getBlockZ())
                    .build();
        }
        return new ItemBuilder(Material.BLUE_WOOL).name("&9Set Blue spawn")
                .lore("&7Click to set the blue team's spawn point",
                        "&7X: " + arena.getBlueSpawn().getBlockX() + " Y: " + arena.getBlueSpawn().getBlockY() + " Z: " + arena.getBlueSpawn().getBlockZ())
                .build();
    }
}
