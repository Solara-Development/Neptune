package dev.lrxh.neptune.providers.database.impl;

import dev.lrxh.neptune.configs.impl.SettingsLocale;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IDatabase {
    String uri = SettingsLocale.URI.getString();
    String database = SettingsLocale.DATABASE.getString();

    IDatabase load();

    CompletableFuture<DataDocument> getUserData(UUID playerUUID);

    CompletableFuture<Void> replace(UUID playerUUID, DataDocument newDocument);

    CompletableFuture<Void> replace(String playerUUID, DataDocument newDocument);

    CompletableFuture<List<DataDocument>> getAllByKitType(String kitName, String type);
}
