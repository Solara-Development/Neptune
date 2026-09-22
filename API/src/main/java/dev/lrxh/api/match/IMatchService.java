package dev.lrxh.api.match;

import dev.lrxh.api.kit.IKit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IMatchService {
    void startMatch(IMatch match, Player redPlayer, Player bluePlayer);

    void startFfaMatch(List<Player> players, IKit kit);

    void startTeamMatch(List<Player> redTeam, List<Player> blueTeam, IKit kit);

    void startDuelMatch(Player redPlayer, Player bluePlayer, IKit kit, boolean duel, int rounds);

    IMatch getMatchApi(Player player);

    IMatch getMatchApi(UUID playerUUID);

    List<IMatch> getActiveMatches();

    void stopMatch(IMatch match);
}
