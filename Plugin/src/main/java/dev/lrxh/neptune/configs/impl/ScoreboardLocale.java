package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum ScoreboardLocale implements IDataAccessor {
    ENABLED_SCOREBOARD("SCOREBOARD.ENABLE", DataType.BOOLEAN, "true"),
    TITLE("SCOREBOARDS.TITLE", DataType.STRING_LIST,
            "&b&l&f&lP&b&lractice",
            "&b&lP&f&lr&b&lactice",
            "&b&lPr&f&la&b&lctice",
            "&b&lPra&f&lc&b&ltice",
            "&b&lPrac&f&lt&b&lice",
            "&b&lPract&f&li&b&lce",
            "&b&lPracti&f&lc&b&le",
            "&b&lPractic&f&le"),

    UPDATE_INTERVAL("SCOREBOARDS.UPDATE-INTERVAL", DataType.INT, "300"),

    LOBBY("SCOREBOARDS.LOBBY", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fOnline: &b<online>",
            "&fIn Fights: &b<in-match>",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    KIT_EDITOR("SCOREBOARDS.KIT_EDITOR", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fOnline: &b<online>",
            "&fIn Fights: &b<in-match>",
            " ",
            "&fEditing: &b<kit>",
            "",
            "&bserver.net",
            "&7&m--------------------"),
    IN_QUEUE("SCOREBOARDS.IN_QUEUE", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fOnline: &b<online>",
            "&fIn Fights: &b<in-match>",
            "&7&m--------------------",
            "&a<kit> Queue",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_STARTING("SCOREBOARDS.IN_GAME.STARTING", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME("SCOREBOARDS.IN_GAME.IN-MATCH", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&aYour Ping: &f<ping>ms",
            "&cTheir Ping: &f<opponent-ping>ms",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_TEAM("SCOREBOARDS.IN_GAME.IN-MATCH-TEAM", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bYour Team: &a<team-alive>&f/&a<team-max>",
            "&bOpponents: &c<opponent-alive>&f/&c<opponent-total>",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_FFA("SCOREBOARDS.IN_GAME.IN-MATCH-FFA", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bAlive: &f<alive>",
            "&bYour Ping: &f<ping>ms",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_BEST_OF("SCOREBOARDS.IN_GAME.BEST_OF", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&aYour Ping: &f<ping>ms",
            "&cTheir Ping: &f<opponent-ping>ms",
            " ",
            "&fYou: &b<points>/<rounds>",
            "&fThem: &b<opponent-points>/<rounds>",
            " ",
            "&bserver.net"),
    IN_GAME_BOXING("SCOREBOARDS.IN_GAME.BOXING", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&bHits: <difference>",
            " &aYou: &f<hits> <combo>",
            " &cTheir: &f<opponent-hits> <opponent-combo>",
            " ",
            "&aYour Ping: &f<ping>ms",
            "&cTheir Ping: &f<opponent-ping>ms",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_BEDWARS("SCOREBOARDS.IN_GAME.BEDWARS", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&bBeds:",
            "&a Your Bed: <bed-status>",
            "&c Opponent Bed: <opponent-bed-status>",
            " ",
            "&aYour Ping: &f<ping>ms",
            "&cTheir Ping: &f<opponent-ping>ms",
            " ",
            "&bserver.net",
            "&7&m--------------------"),

    IN_GAME_BEDWARS_TEAM("SCOREBOARDS.IN_GAME.BEDWARS-TEAM", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bTeams:",
            "&a Your Team: <team-alive>&f/&a<team-total>",
            "&c Enemy Team: <opponent-alive>&f/&c<opponent-total>",
            " ",
            "&bBeds:",
            "&a Your Bed: <team-bed-status>",
            "&c Enemy Bed: <opponent-bed-status>",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_BOXING_TEAM("SCOREBOARDS.IN_GAME.BOXING-TEAM", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bHits:",
            " &aYou: &f<hits> <combo>",
            " ",
            "&aYour Ping: &f<ping>ms",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_BOXING_FFA("SCOREBOARDS.IN_GAME.BOXING-FFA", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bHits:",
            " &aYou: &f<hits> <combo>",
            " ",
            "&aYour Ping: &f<ping>ms",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_SPECTATOR_BEDWARS("SCOREBOARDS.IN_GAME.SPECTATOR-BEDWARS", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bKit: &f<kit>",
            "&bArena: &f<arena>",
            "",
            "&a Red Bed: <red-bed-status>",
            "&9 Blue Bed: <blue-bed-status>",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_SPECTATOR("SCOREBOARDS.IN_GAME.SPECTATOR", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bKit: &f<kit>",
            "&bArena: &f<arena>",
            "",
            "&c<red-name> &7(<red-ping>) vs &9<blue-name> &7(<blue-ping>)",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_SPECTATOR_TEAM("SCOREBOARDS.IN_GAME.SPECTATOR-TEAM", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fRed: &a<red-alive>&f/&a<red-max>",
            "&fBlue: &c<blue-alive>&f/&c<blue-max>",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_SPECTATOR_FFA("SCOREBOARDS.IN_GAME.SPECTATOR-FFA", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bAlive: &f<alive>",
            "&bYour Ping: &f<ping>ms",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_ENDED("SCOREBOARDS.IN_GAME.ENDED", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fMatch Ended",
            " ",
            "&aWinner: &f<winner>",
            "&cLoser: &f<loser>",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    PARTY_LOBBY("SCOREBOARDS.PARTY.LOBBY", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fOnline: &b<online>",
            "&fIn Fights: &b<in-match>",
            " ",
            "&bParty:",
            " &fLeader: &b<party-leader>",
            " &fSize: &b<party-size>/<party-max>",
            " ",
            "&bserver.net",
            "&7&m--------------------");
    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    ScoreboardLocale(String path, DataType dataType, String... defaultValue) {
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
        return ConfigService.get().getScoreboardConfig();
    }
    public void update() {
        IN_GAME_BOXING.set(IN_GAME_BOXING.getStringList().stream().map(str -> 
                str.replaceAll("<diffrence>", "<difference>")
        ).toList());
        PARTY_LOBBY.set(PARTY_LOBBY.getStringList().stream().map(str ->
                str.replaceAll("<leader>", "<party-leader>")
                        .replaceAll("<size>", "<party-size>")
        ).toList());
        IN_SPECTATOR.set(IN_SPECTATOR.getStringList().stream().map(str ->
                str.replaceAll("<playerRed_name>", "<red-name>")
                        .replaceAll("<playerRed_ping>", "<red-ping>")
                        .replaceAll("<playerBlue_name>", "<blue-name>")
                        .replaceAll("<playerBlue_ping>", "<blue-ping>")
        ).toList());
        IN_SPECTATOR_TEAM.set(IN_SPECTATOR_TEAM.getStringList().stream().map(str ->
                str.replaceAll("<alive-red>", "<red-alive>")
                        .replaceAll("<alive-blue>", "<blue-alive>")
                        .replaceAll("<max-red>", "<red-total>")
                        .replaceAll("<max-blue>", "<blue-total>")
        ).toList());
        IN_GAME_TEAM.set(IN_GAME_TEAM.getStringList().stream().map(str ->
                str.replaceAll("<alive>", "<team-alive>")
                        .replaceAll("<max>", "<team-total>")
                        .replaceAll("<alive-opponent>", "<opponent-alive>")
                        .replaceAll("<max-opponent>", "<opponent-total>")
                        .replaceAll("<points>", "<team-points>")
        ).toList());
        IN_GAME_TEAM.set(IN_GAME_TEAM.getStringList().stream().map(str ->
                str.replaceAll("<alive>", "<team-alive>")
                        .replaceAll("<max>", "<team-total>")
                        .replaceAll("<alive-opponent>", "<opponent-alive>")
                        .replaceAll("<max-opponent>", "<opponent-total>")
                        .replaceAll("<points>", "<team-points>")
                        .replaceAll("<opponent-team-bed-status>", "<opponent-bed-status>")
        ).toList());
        getConfigFile().save();
    }
}
