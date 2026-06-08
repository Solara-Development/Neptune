package dev.lrxh.neptune.feature.leaderboard.impl;

import dev.lrxh.neptune.game.kit.Kit;

import java.util.UUID;

public record LeaderboardPlayerEntry(String username, UUID playerUUID, Kit kit) {
}
