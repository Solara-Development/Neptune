package dev.lrxh.api.match.participant;

import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.profile.IProfile;
import dev.lrxh.api.utils.ITime;
import org.bukkit.Location;

import java.util.UUID;

public interface IParticipant {
    boolean isDead();

    UUID getPlayerUUID();

    String getName();

    String getNameColored();

    IParticipant getOpponent();

    IParticipantColor getColor();

    IParticipant getLastAttacker();

    int getHits();

    int getLongestCombo();

    int getCombo();

    boolean isLoser();

    boolean isDisconnected();

    boolean isLeft();

    int getPoints();

    boolean isFrozen();

    boolean isBedBroken();

    ITime getTime();

    int getEloChange();

    Location getCurrentCheckPoint();

    int getCheckPoint();

    Location getSpawn(IMatch match);

    IProfile getProfile();

    String getHitsDifference(IParticipant participant);

    String getHitsDifferenceUncolored(IParticipant otherParticipant);

    IMatch getMatch();

    void reset();

    void toggleFreeze();

    void addWin();

    void playKillEffect();

    void resetCombo();


}
