package dev.lrxh.neptune.feature.leaderboard.impl;

import java.util.UUID;

public record PlayerEntry(String username, UUID uuid, int value) {
}
