package dev.lrxh.neptune.game.arena.procedure;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.menu.ArenaManagementMenu;
import dev.lrxh.neptune.game.arena.menu.ArenasManagementMenu;
import dev.lrxh.neptune.game.arena.menu.WhitelistedBlocksMenu;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public class ArenaProcedureListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        String input = PlainTextComponentSerializer.plainText().serialize(event.message());

        if (input.equalsIgnoreCase("Cancel") && !profile.getArenaProcedure().getType().equals(ArenaProcedureType.NONE)) {
            event.setCancelled(true);
            player.sendMessage(CC.success("Canceled Procedure"));
            profile.getArenaProcedure().setType(ArenaProcedureType.NONE);
            return;
        }

        switch (profile.getArenaProcedure().getType()) {
            case CREATE -> {
                event.setCancelled(true);
                profile.getArenaProcedure().setType(ArenaProcedureType.NONE);

                if (ArenaService.get().getArenaByName(input) != null) {
                    player.sendMessage(CC.error("Arena already exists"));
                    return;
                }

                if (input.contains(" ")) {
                    player.sendMessage(CC.error("Arena name cannot contain spaces"));
                    return;
                }

                Arena arena = new Arena(input, player.getWorld().getTime());

                ArenaService.get().arenas.add(arena);
                player.sendMessage(CC.success("Created arena"));
                new ArenasManagementMenu().open(player);
            }
            case RENAME -> {
                event.setCancelled(true);
                profile.getArenaProcedure().setType(ArenaProcedureType.NONE);
                profile.getArenaProcedure().getArena().setDisplayName(input);
                player.sendMessage(CC.success("Renamed arena"));
                new ArenaManagementMenu(profile.getArenaProcedure().getArena()).open(player);
                profile.getArenaProcedure().setArena(null);
            }
            case SET_SPAWN_RED -> {
                if (!input.equalsIgnoreCase("Done")) return;
                event.setCancelled(true);
                Arena arena = profile.getArenaProcedure().getArena();
                if (!arena.isSetup() || !arena.isDoneLoading()) {
                    profile.getArenaProcedure().setType(ArenaProcedureType.SET_SPAWN_BLUE);
                    player.sendMessage(CC.success("Set arena red spawn"));
                    arena.setRedSpawn(player.getLocation());
                    if (arena.getBlueSpawn() != null) {
                        new NeptuneRunnable() {
                            public void run() {
                                player.teleport(arena.getBlueSpawn());
                            }

                        }.runTask(Neptune.get());
                    }
                    player.sendMessage(CC.info("Go to the spawn of the &9blue &7player and type &aDone"));
                } else {
                    profile.getArenaProcedure().setType(ArenaProcedureType.NONE);
                    arena.setRedSpawn(player.getLocation());
                    new ArenaManagementMenu(arena).open(player);
                    player.sendMessage(CC.success("Set arena red &aspawn"));
                    profile.getArenaProcedure().setArena(null);
                }
            }
            case SET_SPAWN_BLUE -> {
                if (!input.equalsIgnoreCase("Done")) return;
                event.setCancelled(true);
                profile.getArenaProcedure().setType(ArenaProcedureType.NONE);
                Arena arena = profile.getArenaProcedure().getArena();
                if (!arena.isSetup() || !arena.isDoneLoading()) {
                    arena.setBlueSpawn(player.getLocation());
                    profile.getArenaProcedure().setType(ArenaProcedureType.SET_SPAWN_MIN);
                    player.sendMessage(CC.info("Go to the lowest edge of the arena and type &aDone"));
                    return;
                } else {
                    player.sendMessage(CC.success("Set arena blue spawn"));
                }

                arena.setBlueSpawn(player.getLocation());
                new ArenaManagementMenu(arena).open(player);
                profile.getArenaProcedure().setArena(null);
            }
            case SET_SPAWN_MAX -> {
                if (!input.equalsIgnoreCase("Done")) return;
                event.setCancelled(true);
                profile.getArenaProcedure().setType(ArenaProcedureType.NONE);
                Arena arena = profile.getArenaProcedure().getArena();
                if (!arena.isSetup() || !arena.isDoneLoading()) {
                    player.sendMessage(CC.success("Arena setup complete"));
                } else {
                    player.sendMessage(CC.success("Set arena max position"));
                }

                new NeptuneRunnable() {
                    @Override
                    public void run() {
                        arena.setMax(player.getLocation());
                        new ArenaManagementMenu(profile.getArenaProcedure().getArena()).open(player);
                        profile.getArenaProcedure().setArena(null);
                    }
                }.start();
            }
            case SET_BUILD_LIMIT -> {
                if (!input.equalsIgnoreCase("Done")) return;
                event.setCancelled(true);
                profile.getArenaProcedure().setType(ArenaProcedureType.NONE);
                Arena arena = profile.getArenaProcedure().getArena();
                arena.setBuildLimit(player.getLocation().getBlockY());
                new ArenaManagementMenu(profile.getArenaProcedure().getArena()).open(player);
                player.sendMessage(CC.success("Set arena build limit"));
                profile.getArenaProcedure().setArena(null);
            }
            case SET_SPAWN_MIN -> {
                if (!input.equalsIgnoreCase("Done")) return;
                event.setCancelled(true);
                profile.getArenaProcedure().setType(ArenaProcedureType.NONE);
                Arena arena = profile.getArenaProcedure().getArena();
                if (!arena.isSetup() || !arena.isDoneLoading()) {
                    arena.setMin(player.getLocation());
                    profile.getArenaProcedure().setType(ArenaProcedureType.SET_SPAWN_MAX);
                    player.sendMessage(CC.info("Go to the highest edge of the arena and type &aDone"));
                    return;
                }
                new NeptuneRunnable() {
                    @Override
                    public void run() {
                        arena.setMin(player.getLocation());
                        player.sendMessage(CC.success("Set arena min position"));
                        new ArenaManagementMenu(profile.getArenaProcedure().getArena()).open(player);
                        profile.getArenaProcedure().setArena(null);
                    }
                }.start();

            }
            case SET_DEATH_Y -> {
                if (!input.equalsIgnoreCase("Done")) return;
                event.setCancelled(true);
                profile.getArenaProcedure().setType(ArenaProcedureType.NONE);

                profile.getArenaProcedure().getArena().setDeathY(player.getLocation().getBlockY());
                player.sendMessage(CC.success("Set arena death Y position"));
                new ArenaManagementMenu(profile.getArenaProcedure().getArena()).open(player);
                profile.getArenaProcedure().setArena(null);
            }
            case ADD_BLOCK -> {
                if (!input.equalsIgnoreCase("Done")) return;
                event.setCancelled(true);
                if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                    player.sendMessage(CC.error("Item can't be air"));
                    return;
                }

                profile.getArenaProcedure().setType(ArenaProcedureType.NONE);
                Arena arena = profile.getArenaProcedure().getArena();

                arena.getWhitelistedBlocks().add(player.getInventory().getItemInMainHand().getType());
                new WhitelistedBlocksMenu(arena).open(player);
                profile.getArenaProcedure().setArena(null);
            }
            case SET_TIME -> {
                event.setCancelled(true);

                profile.getArenaProcedure().setType(ArenaProcedureType.NONE);
                Arena arena = profile.getArenaProcedure().getArena();
                if (input.equalsIgnoreCase("day")) arena.setTime(1000);
                if (input.equalsIgnoreCase("night")) arena.setTime(13000);
                else if (input.equalsIgnoreCase("noon")) arena.setTime(6000);
                else if (input.equalsIgnoreCase("midnight")) arena.setTime(18000);
                else if (input.equalsIgnoreCase("sunrise")) arena.setTime(23000);
                else if (input.equalsIgnoreCase("sunset")) arena.setTime(12000);
                else if (input.equalsIgnoreCase("current")) arena.setTime(player.getWorld().getTime());
                else {
                    try {
                        long time = Long.parseLong(input);
                        arena.setTime(time);
                    } catch (NumberFormatException e) {
                        player.sendMessage(CC.error("Invalid time input"));
                        return;
                    }
                }
                player.sendMessage(CC.success("Set arena time"));
                new ArenaManagementMenu(arena).open(player);
                profile.getArenaProcedure().setArena(null);
            }
            default -> {}
        }

        ArenaService.get().save();
    }
}
