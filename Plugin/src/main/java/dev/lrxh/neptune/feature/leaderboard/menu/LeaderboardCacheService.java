package dev.lrxh.neptune.feature.leaderboard.menu;

import dev.lrxh.neptune.feature.leaderboard.LeaderboardService;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.feature.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;

import java.util.*;

public class LeaderboardCacheService {
    private static LeaderboardCacheService instance;

    private final Map<LeaderboardType, TypeCache> caches = new HashMap<>();
    private final long cacheDurationMs;

    private LeaderboardCacheService() {
        this.cacheDurationMs = 3000;
    }

    public static LeaderboardCacheService get() {
        if (instance == null) {
            instance = new LeaderboardCacheService();
        }
        return instance;
    }

    public PlayerEntry getPosition(LeaderboardType type, Kit kit, int position) {
        TypeCache cache = caches.computeIfAbsent(type, TypeCache::new);
        return cache.getPosition(kit, position);
    }

    public void clearAll() {
        caches.clear();
    }

    public void clear(LeaderboardType type) {
        caches.remove(type);
    }

    private class TypeCache {
        private final LeaderboardType type;
        private final Map<Kit, List<PlayerEntry>> data = new HashMap<>();
        private long lastRefresh = 0;

        TypeCache(LeaderboardType type) {
            this.type = type;
        }

        PlayerEntry getPosition(Kit kit, int position) {
            refreshIfNeeded();
            List<PlayerEntry> entries = data.getOrDefault(kit, Collections.emptyList());
            int index = position - 1;
            return (index >= 0 && index < entries.size()) ? entries.get(index) : null;
        }

        private void refreshIfNeeded() {
            long now = System.currentTimeMillis();
            if (now - lastRefresh >= cacheDurationMs) {
                refresh();
                lastRefresh = now;
            }
        }

        private void refresh() {
            data.clear();
            LeaderboardService service = LeaderboardService.get();

            for (Kit kit : getVisibleKits()) {
                List<PlayerEntry> entries = service.getPlayerEntries(kit, type);
                data.put(kit, new ArrayList<>(entries));
            }
        }

        private List<Kit> getVisibleKits() {
            return KitService.get().kits.stream()
                    .filter(kit -> !kit.getRules().get(KitRule.HIDDEN))
                    .toList();
        }
    }
}
