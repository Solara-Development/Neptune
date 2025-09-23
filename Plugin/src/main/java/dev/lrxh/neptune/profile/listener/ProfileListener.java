package dev.lrxh.neptune.profile.listener;

import dev.lrxh.api.events.MatchParticipantDeathEvent;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.GithubUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import dev.lrxh.neptune.utils.tasks.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

public class ProfileListener implements Listener {

    @EventHandler
    public void onPreJoin(PlayerLoginEvent event) {
        if (!Neptune.get().isAllowJoin())
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.color("&cDatabasing updating..."));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getName().equals("lrxh_")) {
            player.sendMessage(CC.color("&eThis server is running Neptune version: "
                    + Neptune.get().getDescription().getVersion()));
            player.sendMessage(CC.color("&eCommit: &f" + GithubUtils.getCommitId()));
            player.sendMessage(CC.color("&eMessage: &f" + GithubUtils.getCommitMessage()));
        }
        event.joinMessage(null);

        ProfileService.get().createProfile(player)
                .thenAccept(unused -> TaskScheduler.get().startTaskCurrentTick(new NeptuneRunnable() {
                    @Override
                    public void run() {
                        PlayerUtil.teleportToSpawn(player.getUniqueId());

                        if (!MessagesLocale.JOIN_MESSAGE.getString().equals("NONE")) {
                            ServerUtils.broadcast(MessagesLocale.JOIN_MESSAGE,
                                    new Replacement("<player>", player.getName()));
                        }
                        PlayerUtil.reset(player);
                        HotbarService.get().giveItems(player);
                    }
                }));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        onQuit(new PlayerQuitEvent(event.getPlayer(), event.reason()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        if (profile == null)
            return;
        Match match = profile.getMatch();

        if (match != null) {
            if (profile.getState() == ProfileState.IN_SPECTATOR) {
                match.removeSpectator(player.getUniqueId(), true);
            } else {
                Participant participant = match.getParticipant(player.getUniqueId());
                if (participant == null)
                    return;
                match.onLeave(match.getParticipant(player), true);
                MatchParticipantDeathEvent deathEvent = new MatchParticipantDeathEvent(match, participant,
                        participant.getDeathCause().getMessage().getString());
                Bukkit.getPluginManager().callEvent(deathEvent);
            }
        }

        if (!MessagesLocale.LEAVE_MESSAGE.getString().equals("NONE")) {
            ServerUtils.broadcast(MessagesLocale.LEAVE_MESSAGE, new Replacement("<player>", player.getName()));
        }

        ProfileService.get().removeProfile(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = API.getProfile(player);
        if (profile == null)
            return;
        if (profile.hasState(ProfileState.IN_KIT_EDITOR)) {
            Kit kit = profile.getGameData().getKitEditor();

            profile.getGameData().get(kit)
                    .setKitLoadout(Arrays.asList(player.getInventory().getContents()));

            MessagesLocale.KIT_EDITOR_STOP.send(player.getUniqueId(), new Replacement("<kit>", kit.getDisplayName()));

            if (profile.getGameData().getParty() == null) {
                profile.setState(ProfileState.IN_LOBBY);
            } else {
                profile.setState(ProfileState.IN_PARTY);
            }
        }
    }
}
