package dev.lrxh.neptune.feature.leaderboard.impl;

import dev.lrxh.neptune.game.kit.Kit;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public record LeaderboardPlayerEntry(String username, UUID playerUUID, Kit kit) {
}
