package dev.lrxh.api.leaderboard;

import dev.lrxh.api.kit.IKit;

import java.util.List;
import java.util.UUID;

public interface ILeaderboardService {
    List<ILeaderboardEntry> getTopPlayers(IKit kit, ILeaderboardType type, int limit);

    int getPlayerRank(UUID playerUUID, IKit kit, ILeaderboardType type);

    /**
     * The loading is asynchronous.
     */
    void reload();
}
