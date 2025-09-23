package dev.lrxh.neptune.providers.database.impl;

import lombok.Getter;

@Getter
public enum DatabaseType {
    MONGO(new MongoDatabase()),
    SQLITE(new SQLiteDatabase());

    private final IDatabase iDatabase;

    DatabaseType(IDatabase iDatabase) {
        this.iDatabase = iDatabase;
    }
}
