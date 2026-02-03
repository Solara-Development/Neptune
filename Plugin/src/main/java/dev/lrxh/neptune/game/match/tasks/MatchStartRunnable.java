package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.api.events.MatchStartEvent;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SoundsLocale;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.Time;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MatchStartRunnable extends NeptuneRunnable {

    private final Match match;
    private int startTimer;

    public MatchStartRunnable(Match match) {
        this.match = match;
        this.startTimer = match instanceof FfaFightMatch ? 5 : 3;

        match.teleportToPositions();
        match.setupParticipants();
        match.checkRules();

        match.getTime().setStop(true);
        match.getTime().setZero();
    }

    @Override
    public void run() {
        if (match.isEnded()) {
            stop();
            return;
        }

        if (startTimer == 0) {
            match.sendMessage(MessagesLocale.MATCH_STARTED);
            match.startMatch();
            match.checkRules();
            checkFollowings();
            match.getTime().setStop(false);

            for (Participant participant : match.getParticipantsList()) {
                participant.setTime(new Time());
            }

            stop();
            MatchStartEvent event = new MatchStartEvent(match);
            Bukkit.getPluginManager().callEvent(event);
            return;
        }
        if (match.getState().equals(MatchState.STARTING)) {
            match.playSound(SoundsLocale.getSound(SoundsLocale.MATCH_START_COUNTDOWN));
            match.sendTitle(
                    CC.color(MessagesLocale.MATCH_STARTING_TITLE_HEADER.getString().replace("<countdown-time>",
                            String.valueOf(startTimer))),
                    CC.color(MessagesLocale.MATCH_STARTING_TITLE_FOOTER.getString().replace("<countdown-time>",
                            String.valueOf(startTimer))),
                    19);
            match.sendMessage(MessagesLocale.MATCH_STARTING, new Replacement("<timer>", String.valueOf(startTimer)));
        }
        startTimer--;

    }

    private void checkFollowings() {
        for (Participant participant : match.getParticipantsList()) {
            if (participant.isDisconnected()) continue;
            SettingData settingData = API.getProfile(participant.getPlayerUUID()).getSettingData();
            if (settingData == null) continue;
            if (settingData.getFollowings().isEmpty()) continue;

            for (UUID uuid : settingData.getFollowings()) {
                Player follower = Bukkit.getPlayer(uuid);
                if (follower == null) continue;

                if (API.getProfile(follower).getState().equals(ProfileState.IN_GAME)) continue;

                Player particpiantPlayer = participant.getPlayer();
                if (particpiantPlayer == null) continue;

                match.addSpectator(follower, particpiantPlayer, false, true);
            }
        }
    }
}
