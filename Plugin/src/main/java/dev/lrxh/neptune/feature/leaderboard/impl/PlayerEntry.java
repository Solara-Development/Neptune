package dev.lrxh.neptune.feature.leaderboard.impl;

import lombok.AllArgsConstructor;

import java.util.UUID;

public record PlayerEntry(String username, UUID uuid, int value) {
}
