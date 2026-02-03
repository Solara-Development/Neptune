package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.api.events.MatchNewRoundStartEvent;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SoundsLocale;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;

public class MatchSecondRoundRunnable extends NeptuneRunnable {

    private final Match match;
    private final Participant participant;
    private int respawnTimer = 3;

    public MatchSecondRoundRunnable(Match match, Participant participant) {
        this.match = match;
        this.participant = participant;

        match.setupParticipants();
        match.checkRules();
        match.teleportToPositions();
    }

    @Override
    public void run() {
        if (!MatchService.get().matches.contains(match) || participant.isLeft()) {
            stop();
            return;
        }

        if (match.isEnded()) {
            stop();
            return;
        }

        if (participant.getPlayer() == null) return;
        if (respawnTimer == 0) {
            match.startMatch();
            match.checkRules();
            match.sendMessage(MessagesLocale.ROUND_STARTED);
            stop();
            return;
        }

        if (match.getState().equals(MatchState.STARTING)) {
            match.playSound(SoundsLocale.getSound(SoundsLocale.MATCH_START_COUNTDOWN));

            match.sendTitle(CC.color(MessagesLocale.MATCH_STARTING_TITLE_HEADER.getString().replace("<countdown-time>", String.valueOf(respawnTimer))),
                    CC.color(MessagesLocale.MATCH_STARTING_TITLE_FOOTER.getString().replace("<countdown-time>", String.valueOf(respawnTimer))),
                    19);
            match.sendMessage(MessagesLocale.ROUND_STARTING, new Replacement("<timer>", String.valueOf(respawnTimer)));
        }

        if (respawnTimer == 3) {
            match.setCurrentRound(match.getCurrentRound() + 1);
            match.setupParticipants();
            match.teleportToPositions();

            if (match.getKit().is(KitRule.RESET_ARENA_AFTER_ROUND)) {
                match.resetArena();
            }
            MatchNewRoundStartEvent event = new MatchNewRoundStartEvent(match);
            Bukkit.getPluginManager().callEvent(event);
        }
        respawnTimer--;
    }
}
