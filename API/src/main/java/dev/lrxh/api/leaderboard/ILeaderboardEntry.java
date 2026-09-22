package dev.lrxh.api.leaderboard;

import java.util.UUID;

public interface ILeaderboardEntry {
    String getUsername();

    UUID getUuid();

    int getValue();
}
