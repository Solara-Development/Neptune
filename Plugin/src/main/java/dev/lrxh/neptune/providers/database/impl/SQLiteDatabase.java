package dev.lrxh.neptune.providers.database.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class SQLiteDatabase implements IDatabase {
    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS playerData (" +
            "uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
            "data TEXT NOT NULL" +
            ")";
    private static final String SQL_UPSERT = "INSERT INTO playerData(uuid, data) VALUES (?, ?) " +
            "ON CONFLICT(uuid) DO UPDATE SET data = excluded.data";
    private final String dbPath;
    private final ExecutorService dbExecutor;
    private Connection connection;

    public SQLiteDatabase() {
        this.dbPath = "jdbc:sqlite:" + Neptune.get().getDataFolder() + "/neptune.db";
        this.dbExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "neptune-sqlite-db");
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public IDatabase load() {
        try {
            this.connection = DriverManager.getConnection(dbPath);
            Future<?> initFuture = dbExecutor.submit(() -> {
                configureConnectionPragmas();
                createTableIfNotExists();
            });
            initFuture.get();
        } catch (SQLException | InterruptedException | ExecutionException e) {
            ServerUtils.error("Failed to connect / initialize SQLite database: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(Neptune.get());
        }
        return this;
    }

    private void configureConnectionPragmas() {
        try (Statement s = connection.createStatement()) {
            s.execute("PRAGMA journal_mode = WAL;");
            s.execute("PRAGMA synchronous = NORMAL;");
            s.execute("PRAGMA temp_store = MEMORY;");
            s.execute("PRAGMA cache_size = 10000;");
            s.execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            ServerUtils.error("Failed to apply SQLite pragmas: " + e.getMessage());
        }
    }

    private void ensureConnectionOpen() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbPath);
            configureConnectionPragmas();
        }
    }

    private void createTableIfNotExists() {
        try {
            ensureConnectionOpen();
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(SQL_CREATE_TABLE);
            }
        } catch (SQLException e) {
            ServerUtils.error("Error creating playerData table: " + e.getMessage());
        }
    }

    public CompletableFuture<List<Map<String, Object>>> queryData(String sql) {
        return CompletableFuture.supplyAsync(() -> {
            List<Map<String, Object>> results = new ArrayList<>();
            try {
                ensureConnectionOpen();
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {

                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>(columnCount);
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(meta.getColumnName(i), rs.getObject(i));
                        }
                        results.add(row);
                    }
                }
            } catch (SQLException e) {
                ServerUtils.error("Error executing query on SQLite: " + e.getMessage());
                throw new RuntimeException(e);
            }
            return results;
        }, dbExecutor);
    }

    public CompletableFuture<Integer> executeUpdate(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ensureConnectionOpen();
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    for (int i = 0; i < params.length; i++) {
                        ps.setObject(i + 1, params[i]);
                    }
                    return ps.executeUpdate();
                }
            } catch (SQLException e) {
                ServerUtils.error("Error executing update on SQLite: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }, dbExecutor);
    }

    @Override
    public CompletableFuture<DataDocument> getUserData(UUID playerUUID) {
        return queryData("SELECT data FROM playerData WHERE uuid = '" + playerUUID + "'")
                .thenApply(list -> {
                    if (list.isEmpty())
                        return null;
                    String dataString = (String) list.get(0).get("data");
                    return new DataDocument(dataString);
                });
    }

    @Override
    public CompletableFuture<Void> replace(UUID playerUUID, DataDocument newDocument) {
        return executeUpdate(SQL_UPSERT, playerUUID.toString(), newDocument.toDocument().toJson())
                .thenApply(ignored -> null);
    }

    @Override
    public CompletableFuture<Void> replace(String playerUUID, DataDocument newDocument) {
        return executeUpdate(SQL_UPSERT, playerUUID, newDocument.toDocument().toJson())
                .thenApply(ignored -> null);
    }

    @Override
    public CompletableFuture<List<DataDocument>> getAllByKitType(String kitName, String type) {
        String sql = "SELECT data FROM playerData " +
                "WHERE json_extract(data, '$.kitData.\"" + kitName + "\"." + type + "') IS NOT NULL " +
                "AND json_extract(data, '$.kitData.\"" + kitName + "\"." + type + "') > 0 " +
                "ORDER BY CAST(json_extract(data, '$.kitData.\"" + kitName + "\"." + type + "') AS INTEGER) DESC";

        return queryData(sql).thenApply(resultList -> {
            List<DataDocument> results = new ArrayList<>();
            for (Map<String, Object> row : resultList) {
                String jsonString = (String) row.get("data");
                if (jsonString != null) {
                    results.add(new DataDocument(jsonString));
                }
            }
            return results;
        });
    }

}
