package dev.lrxh.neptune.feature.leaderboard.impl;

import dev.lrxh.neptune.profile.data.KitData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
@AllArgsConstructor
public enum LeaderboardType {
    KILLS("Kills", "KILLS", "WINS") {
        @Override
        public int get(KitData kitData) {
            return kitData.getKills();
        }
    },
    BEST_WIN_STREAK("Best Win Streak", "BEST_WIN_STREAK", "WIN_STREAK_BEST") {
        @Override
        public int get(KitData kitData) {
            return kitData.getBestStreak();
        }
    },
    ELO("Elo", "ELO", "ELO") {
        @Override
        public int get(KitData kitData) {
            return kitData.getElo();
        }
    },
    DEATHS("Deaths", "DEATHS", "LOSSES") {
        @Override
        public int get(KitData kitData) {
            return kitData.getDeaths();
        }
    };

    private final String name;
    private final String configName;
    private final String databaseName;

    @Nullable
    public static LeaderboardType value(String value) {
        for (LeaderboardType leaderboardType : values()) {
            if (leaderboardType.toString().equalsIgnoreCase(value)) {
                return leaderboardType;
            }
        }

        return null;
    }

    public abstract int get(KitData kitData);
}
