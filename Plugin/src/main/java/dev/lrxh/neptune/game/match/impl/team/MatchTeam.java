package dev.lrxh.neptune.game.match.impl.team;

import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.api.match.team.IMatchTeam;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import lombok.Data;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Data
public class MatchTeam implements IMatchTeam {
    private final List<Participant> participants;
    private final List<Participant> deadParticipants;

    private MatchTeam opponentTeam;

    private boolean bedBroken;
    private int points;

    public MatchTeam(List<Participant> participants) {
        this.participants = participants;
        this.deadParticipants = new ArrayList<>();
    }

    public List<IParticipant> getParticipants() {
        return participants.stream()
                .map(p -> (IParticipant) p)
                .toList();
    }

    public List<IParticipant> getDeadParticipants() {
        return deadParticipants.stream()
                .map(p -> (IParticipant) p)
                .toList();
    }

    public List<Participant> deadParticipants() {
        return deadParticipants;
    }

    public List<Participant> participants() {
        return participants;
    }

    public void setBedBroken(boolean bedBroken) {
        this.bedBroken = bedBroken;
        forEachParticipant(participants -> participants.setBedBroken(bedBroken));
    }

    public boolean isLoser() {
        return deadParticipants.size() >= participants.size();
    }

    public int getAliveParticipants() {
        return participants.size() - deadParticipants.size();
    }

    public void sendTitle(TextComponent header, TextComponent footer, int duration) {
        forEachParticipant((participant) -> participant.sendTitle(header, footer, duration));
    }

    public String getTeamNames() {
        StringBuilder playerNames = new StringBuilder();
        for (Participant participant : participants) {
            if (!playerNames.isEmpty()) {
                playerNames.append(MessagesLocale.MATCH_COMMA.getString());
            }
            playerNames.append(participant.getNameUnColored());
        }
        return playerNames.toString();
    }

    public void forEachParticipant(Consumer<Participant> action) {
        for (Participant participant : participants) {
            Player player = participant.getPlayer();
            if (player != null) {
                if (participant.isLeft() || participant.isDisconnected())
                    continue;
                action.accept(participant);
            }
        }
    }

    public void addPoint() {
        points++;
        forEachParticipant(Participant::addWin);
    }
}
