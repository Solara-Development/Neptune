package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.api.events.MatchEndEvent;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.UUID;

public class MatchEndRunnable extends NeptuneRunnable {
    private final Match match;
    private int endTimer = 3;

    public MatchEndRunnable(Match match) {
        this.match = match;

        match.getTime().setStop(true);
    }

    @Override
    public void run() {
        if (!MatchService.get().matches.contains(match)) {
            stop();
            return;
        }
        if (endTimer == 0) {
            match.setState(MatchState.ENDING);
            if (match.getKit().is(KitRule.SHOW_HP)) {
                match.hideHealth();
            }

            for (UUID spectator : new HashSet<>(match.spectators)) {
                match.removeSpectator(spectator, false);
            }

            match.resetArena();

            match.forEachParticipant(participant -> {

                Profile profile = API.getProfile(participant.getPlayerUUID());
                if (profile.getMatch() != match) return;

                PlayerUtil.reset(participant.getPlayer());
                profile.setMatch(null);
                PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
                profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);

                match.forEachPlayer(player -> HotbarService.get().giveItems(player));
            });

            match.sendEndMessage();
            match.getArena().remove();
            MatchService.get().matches.remove(match);
            MatchEndEvent event = new MatchEndEvent(match);
            Bukkit.getPluginManager().callEvent(event);
            stop();
        }
        endTimer--;
    }
}
