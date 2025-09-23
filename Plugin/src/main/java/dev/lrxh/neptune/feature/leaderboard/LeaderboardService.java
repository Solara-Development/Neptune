package dev.lrxh.neptune.feature.leaderboard;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardPlayerEntry;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.feature.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.providers.database.DatabaseService;
import dev.lrxh.neptune.providers.database.impl.DataDocument;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class LeaderboardService {
    private static LeaderboardService instance;
    public final Pattern PATTERN = Pattern.compile("(KILLS|BEST_WIN_STREAK|DEATHS|ELO)_(.*)_(10|[1-9])_(name|value)");
    private final List<LeaderboardPlayerEntry> changes;
    private final Map<Kit, Map<LeaderboardType, List<PlayerEntry>>> leaderboards;

    public LeaderboardService() {
        leaderboards = new ConcurrentHashMap<>();
        changes = new ArrayList<>();
    }

    public static LeaderboardService get() {
        if (instance == null)
            instance = new LeaderboardService();
        return instance;
    }

    public String getPlaceholder(String placeholder) {
        Matcher matcher = PATTERN.matcher(placeholder);
        if (matcher.matches()) {
            String type = matcher.group(1);
            String kitName = matcher.group(2);
            int entry = Integer.parseInt(matcher.group(3));
            boolean name = matcher.group(4).equals("name");

            Kit kit = KitService.get().getKitByName(kitName);
            if (kit == null)
                return placeholder;

            LeaderboardType leaderboardType = LeaderboardType.value(type);
            PlayerEntry playerEntry = getLeaderboardSlot(kit, leaderboardType, entry);

            if (playerEntry == null)
                return "???";
            return name ? playerEntry.getUsername() : String.valueOf(playerEntry.getValue());
        }
        return placeholder;
    }

    private void checkIfMissing() {
        for (Kit kit : KitService.get().kits) {
            leaderboards.computeIfAbsent(kit, k -> {
                Map<LeaderboardType, List<PlayerEntry>> typeMap = new ConcurrentHashMap<>();
                for (LeaderboardType leaderboardType : LeaderboardType.values()) {
                    typeMap.put(leaderboardType, new ArrayList<>());
                }
                return typeMap;
            });
        }
    }

    public PlayerEntry getLeaderboardSlot(Kit kit, LeaderboardType leaderboardType, int i) {
        List<PlayerEntry> playerEntries = getPlayerEntries(kit, leaderboardType);
        if (i <= 0 || i > playerEntries.size())
            return null;
        return playerEntries.get(i - 1);
    }

    public List<PlayerEntry> getPlayerEntries(Kit kit, LeaderboardType leaderboardType) {
        Map<LeaderboardType, List<PlayerEntry>> kitLeaderboards = leaderboards.get(kit);
        if (kitLeaderboards == null)
            return Collections.emptyList();

        List<PlayerEntry> entries = kitLeaderboards.get(leaderboardType);
        if (entries == null)
            return Collections.emptyList();

        List<PlayerEntry> sortedEntries = new ArrayList<>(entries);
        sortedEntries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        return sortedEntries;
    }

    public CompletableFuture<Void> load() {
        checkIfMissing();

        for (Map<LeaderboardType, List<PlayerEntry>> kitMap : leaderboards.values()) {
            for (List<PlayerEntry> entries : kitMap.values()) {
                entries.clear();
            }
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (LeaderboardType type : LeaderboardType.values()) {
            futures.add(loadType(type));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<Void> loadType(LeaderboardType leaderboardType) {
        return CompletableFuture.runAsync(() -> {
            for (Kit kit : KitService.get().getKits()) {
                DatabaseService.get().getDatabase().getAllByKitType(kit.getName(), leaderboardType.getDatabaseName())
                        .thenAccept(documents -> {
                            List<PlayerEntry> tempEntries = new ArrayList<>();

                            for (DataDocument document : documents) {
                                String username = document.getString("username");
                                UUID uuid = UUID.fromString(document.getString("uuid"));

                                KitData kitData = getKitData(document, kit);

                                if (kitData == null)
                                    continue;

                                PlayerEntry playerEntry = new PlayerEntry(username, uuid, leaderboardType.get(kitData));
                                tempEntries.add(playerEntry);
                            }

                            tempEntries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

                            Map<LeaderboardType, List<PlayerEntry>> kitLeaderboards = leaderboards.get(kit);
                            if (kitLeaderboards != null) {
                                List<PlayerEntry> currentEntries = kitLeaderboards.get(leaderboardType);
                                currentEntries.clear();
                                currentEntries.addAll(tempEntries);
                            }
                        }).exceptionally(throwable -> {
                            ServerUtils.error("Failed to load leaderboard: " + throwable.getMessage());
                            throwable.printStackTrace();
                            return null;
                        });
            }
        });
    }

    public CompletableFuture<Void> update() {
        if (changes.isEmpty())
            return CompletableFuture.completedFuture(null);

        checkIfMissing();
        List<LeaderboardPlayerEntry> copy = new ArrayList<>(changes);
        changes.clear();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (LeaderboardPlayerEntry leaderboardPlayerEntry : copy) {
            for (LeaderboardType type : LeaderboardType.values()) {
                futures.add(loadLB(type, leaderboardPlayerEntry));
            }
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void addOrUpdatePlayerEntry(Kit kit, PlayerEntry newEntry, LeaderboardType leaderboardType) {
        synchronized (leaderboards) {
            Map<LeaderboardType, List<PlayerEntry>> kitLeaderboards = leaderboards.get(kit);
            if (kitLeaderboards == null)
                return;

            List<PlayerEntry> entries = kitLeaderboards.get(leaderboardType);
            if (entries == null) {
                entries = new ArrayList<>();
                kitLeaderboards.put(leaderboardType, entries);
            }

            entries.removeIf(e -> e.getUuid().equals(newEntry.getUuid()));

            entries.add(newEntry);

            entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

            if (entries.size() > 10) {
                entries.subList(10, entries.size()).clear();
            }
        }
    }

    public void addChange(LeaderboardPlayerEntry playerEntry) {
        changes.add(playerEntry);
    }

    private CompletableFuture<Void> loadLB(LeaderboardType leaderboardType,
                                           LeaderboardPlayerEntry leaderboardPlayerEntry) {
        Kit kit = leaderboardPlayerEntry.getKit();
        UUID playerUUID = leaderboardPlayerEntry.getPlayerUUID();
        String username = leaderboardPlayerEntry.getUsername();

        return getKitData(playerUUID, kit).thenAccept(kitData -> {
            if (kitData == null)
                return;

            PlayerEntry playerEntry = new PlayerEntry(username, playerUUID, leaderboardType.get(kitData));
            addOrUpdatePlayerEntry(kit, playerEntry, leaderboardType);
        });
    }

    private KitData getKitData(DataDocument document, Kit kit) {
        if (document == null)
            return null;

        DataDocument kitStatistics = document.getDataDocument("kitData");
        if (kitStatistics == null)
            return null;

        DataDocument kitDocument = kitStatistics.getDataDocument(kit.getName());
        if (kitDocument == null)
            return null;

        KitData kitData = new KitData();
        kitData.setCurrentStreak(kitDocument.getInteger("WIN_STREAK_CURRENT", 0));
        kitData.setKills(kitDocument.getInteger("WINS", 0));
        kitData.setDivision(DivisionService.get().getDivisionByElo(kitData.getKills()));
        kitData.setDeaths(kitDocument.getInteger("LOSSES", 0));
        kitData.setBestStreak(kitDocument.getInteger("WIN_STREAK_BEST", 0));

        return kitData;
    }

    private CompletableFuture<KitData> getKitData(UUID playerUUID, Kit kit) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            return CompletableFuture.completedFuture(
                    API.getProfile(player).getGameData().get(kit));
        }

        return DatabaseService.get().getDatabase().getUserData(playerUUID)
                .thenApply(document -> getKitData(document, kit));
    }
}