package dev.lrxh.api.match;

import org.bukkit.entity.Player;

public interface IMatchService {
    void startMatch(IMatch match, Player redPlayer, Player bluePlayer);
}
