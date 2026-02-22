package dev.lrxh.api.match;

import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.api.match.team.IMatchTeam;

import java.util.List;
import java.util.UUID;

public interface ITeamFightMatch extends IMatch {
    List<IParticipant> getParticipants();

    IMatchTeam getRedTeam();

    IMatchTeam getBlueTeam();

    IMatchTeam getWinner();

    IMatchTeam getLoser();

    IMatchTeam getParticipantTeam(IParticipant participant);

    boolean onSameTeam(UUID playerUUID, UUID otherUUID);
}
