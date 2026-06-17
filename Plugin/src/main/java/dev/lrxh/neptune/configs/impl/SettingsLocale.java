package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum SettingsLocale implements IDataAccessor {
    FRIENDLY_FIRE("FRIENDLY_FIRE", DataType.BOOLEAN, "false"),
    MATCH_START_COUNTDOWN("MATCH_START_COUNTDOWN", "The countdown in seconds before a match starts.", DataType.INT, "3"),
    FFA_MATCH_START_COUNTDOWN("FFA_MATCH_START_COUNTDOWN", "The countdown in seconds before an FFA match starts.", DataType.INT, "5"),
    DYNAMIC_ARENA_GENERATION("DYNAMIC_ARENA_GENERATION",
            "If disabled, matches reuse the original arena (flagged in use) instead of generating a copy. Requires a server restart to apply.",
            DataType.BOOLEAN, "false"),
    DUPLICATE_WORLD("DUPLICATE.WORLD", "World that holds all pre-generated arena duplicates (used when ARENA_GENERATION is disabled).", DataType.STRING, "neptune_duplicates"),
    DUPLICATE_DISTANCE("DUPLICATE.DISTANCE", "Blocks between each duplicate arena on the grid. Must be larger than your largest arena's footprint.", DataType.INT, "500"),
    ARENA_CLEANUP_METHOD("ARENA_CLEANUP_METHOD", "Engine used to reset arenas after a match: FAWE (default, requires FastAsyncWorldEdit) or BLOCKCHANGER. Requires a server restart to apply.", DataType.STRING, "FAWE"),
    COMMANDS_AFTER_MATCH_WINNER("COMMAND_AFTER_MATCH.WINNER", DataType.STRING_LIST, "NONE"),
    COMMANDS_AFTER_MATCH_LOSER("COMMAND_AFTER_MATCH.LOSER", DataType.STRING_LIST, "NONE"),
    SPAWN_LOCATION("SPAWN.LOCATION", DataType.STRING, "NONE"),
    LEADERBOARD_UPDATE_TIME("LEADERBOARD.UPDATE_TIME",
            "How often leaderboards should check in ticks (20 ticks = 1 second).", DataType.INT, "10"),
    LEADERBOARD_MAX_POSITIONS("LEADERBOARD.MAX_POSITIONS",
            "Maximum number of leaderboard positions to display.", DataType.INT, "10"),
    DATABASE_TYPE("DATABASE.TYPE", "Database Type. MONGO, SQLITE", DataType.STRING, "SQLITE"),
    URI("DATABASE.URI", "Connection URI.", DataType.STRING, "NONE"),
    DATABASE("DATABASE.DATABASE_NAME", "Database Name", DataType.STRING, "neptune"),
    PARTICIPANT_COLOR_BLUE("PARTICIPANT.COLOR.BLUE", "", DataType.STRING, "&9"),
    PARTICIPANT_COLOR_RED("PARTICIPANT.COLOR.RED", "", DataType.STRING, "&c"),
    REQUEST_EXPIRY_TIME("REQUEST.EXPIRY_TIME", "How long a request should last in seconds.", DataType.INT, "30"),
    VOID_Y_LOCATION("VOID_Y_LOCATION", "The Y level at which players are considered to be in the void and will be teleported back to spawn.", DataType.INT,
            "-64"),
    STARTING_ELO("STARTING_ELO", "The starting ELO rating for new players. Requires a server restart or full profile reset to apply to existing players.", DataType.INT, "0"),
    IN_MATCH_BLOCKED_COMMANDS("IN_MATCH.BLOCK_COMMANDS", "Commands which the player can't run during a match.",
            DataType.STRING_LIST,
            ""),
    PARTY_ADVERTISE_TIME("PARTY.ADVERTISE_TIME",
            "The time it should take for sending each message for party advertisements, in ticks (20 ticks = 1 second).",
            DataType.INT, "6000"),
    EVENT_AUTO_SCHEDULE_ENABLED("EVENT.AUTO_SCHEDULE.ENABLED", DataType.BOOLEAN, "false"),
    EVENT_AUTO_SCHEDULE_INTERVAL("EVENT.AUTO_SCHEDULE.INTERVAL", "Interval in ticks between auto events.", DataType.INT, "72000"),
    EVENT_MIN_PLAYERS("EVENT.MIN_PLAYERS", DataType.INT, "2"),
    EVENT_START_COOLDOWN("EVENT.START_COOLDOWN", "Cooldown in seconds before a player can start another event. Set to 0 to disable.", DataType.INT, "300"),
    VERSION("VERSION", "Do not change this!", DataType.INT, "1");

    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    SettingsLocale(String path, @Nullable String comment, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = comment;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    SettingsLocale(String path, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = null;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    @Override
    public String getHeader() {
        return "";
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getMainConfig();
    }

    public void update() {
    }
}