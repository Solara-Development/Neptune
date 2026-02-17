package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class ArenaBoundaryCheckTask extends NeptuneRunnable {
    @Override
    public void run() {
        for (Match match : MatchService.get().matches) {
            if (match.isEnded())
                continue;
            if (match.getState() != MatchState.IN_ROUND)
                continue;

            for (Participant participant : match.getParticipantsList()) {
                Arena arena = match.getArena();

                Player player = participant.getPlayer();
                if (player == null || !player.isOnline() || participant.isDead()
                        || player.getGameMode().equals(GameMode.SPECTATOR))
                    continue;
                Profile profile = API.getProfile(player);
                if (profile == null || profile.getMatch() != match)
                    continue;

                if (profile.getState().equals(ProfileState.IN_SPECTATOR) || participant.isDead())
                    continue;

                if (!LocationUtil.isInside(player.getLocation(), arena.getMin(), arena.getMax())) {
                    if (match.getKit().is(KitRule.PARKOUR)) {
                        player.teleport(participant.getSpawn(match));
                        return;
                    }

                    participant.setDeathCause(DeathCause.DIED);
                    match.onDeath(participant);
                    participant.sendTitle(MessagesLocale.MATCH_OUT_OF_BOUNDS_TITLE_HEADER,
                            MessagesLocale.MATCH_OUT_OF_BOUNDS_TITLE_FOOTER, 10);
                    return;
                }

                if (player.getLocation().getBlockY() > arena.getBuildLimit() + 1) {
                    if (match.getKit().is(KitRule.PARKOUR)) {
                        player.teleport(participant.getSpawn(match));
                        return;
                    }

                    if (profile.getState().equals(ProfileState.IN_SPECTATOR) || participant.isDead())
                        continue;

                    player.damage(5);
                    participant.sendTitle(MessagesLocale.MATCH_OUT_OF_BOUNDS_TITLE_HEADER,
                            MessagesLocale.MATCH_OUT_OF_BOUNDS_TITLE_FOOTER, 10);
                }
            }
        }
    }
}
