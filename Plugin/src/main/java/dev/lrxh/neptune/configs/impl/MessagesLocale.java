package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public enum MessagesLocale implements IDataAccessor {
    MATCH_YOU("MATCH.YOU", DataType.STRING, "You"),
    MATCH_OPPONENT_TEAM("MATCH.OPPONENT_TEAM", DataType.STRING, "Opponent Team"),
    MATCH_DEATH_DISCONNECT("MATCH.DEATH.DISCONNECT", DataType.STRING_LIST, "<player> &7disconnected"),
    MATCH_DEATH_KILLED("MATCH.DEATH.KILLED", DataType.STRING_LIST, "<player> &7was killed by <killer>"),
    MATCH_DEATH_DIED("MATCH.DEATH.DIED", DataType.STRING_LIST, "<player> &7died"),
    MATCH_DEATH_VOID("MATCH.DEATH.VOID", DataType.STRING_LIST, "<player> &7fell into the void while fighting <killer>"),
    QUEUE_JOIN("QUEUE.JOIN", DataType.STRING_LIST, "&7(&bDuels&7) Joined Queue"),
    QUEUE_LEAVE("QUEUE.LEAVE", DataType.STRING_LIST, "&7(&bDuels&7) Left queue"),
    QUEUE_ACTION_BAR("QUEUE.ACTION_BAR", DataType.STRING, "&7<kit> 1v1 &8| &a<time>"),
    QUEUE_REPEAT("QUEUE.REPEAT.MESSAGE", DataType.STRING_LIST, "&aSearching for other players in queue..."),
    QUEUE_REPEAT_TOGGLE("QUEUE.REPEAT.TOGGLE", DataType.BOOLEAN, "true"),
    MATCH_STARTED("MATCH.STARTED", DataType.STRING_LIST, "&aMatch Started!"),
    ROUND_STARTED("MATCH.ROUND.STARTED", DataType.STRING_LIST, "&aRound Started!"),
    MATCH_FOUND("MATCH.FOUND", DataType.STRING_LIST, " ", "&a&lMatch Found!", " ", "&fKit: &a<kit>",
            "&fOpponent: &a<opponent>", "&fPing: &b<opponent-ping>", "&fOpponent Elo: &a<opponent-elo>", "", " "),
    MATCH_STARTING("MATCH.START.TIMER", DataType.STRING_LIST, "&fMatch starting in &b<timer>&f..."),
    MATCH_STARTING_TITLE_HEADER("MATCH.STARTING.TITLE-HEADER", DataType.STRING, "&e<countdown-time>"),
    MATCH_OUT_OF_BOUNDS_TITLE_HEADER("MATCH.OUT_OF_BOUNDS.TITLE-HEADER", DataType.STRING, "&cYOU ARE OUT OF BOUNDS!"),
    MATCH_OUT_OF_BOUNDS_TITLE_FOOTER("MATCH.OUT_OF_BOUNDS.TITLE-FOOTER", DataType.STRING,
            "&cYou will start taking damage!"),
    RANKUP_TITLE_HEADER("RANKUP.TITLE-HEADER", DataType.STRING, "&b&lPROMOTED!"),
    RANKUP_TITLE_FOOTER("RANKUP.TITLE-FOOTER", DataType.STRING, "&7You are now in &f<division>&7!"),
    RANKUP_MESSAGE("RANKUP.MESSAGE", DataType.STRING_LIST,
            "&aCongratulations! &7You've been promoted to &f<division>&7."),
    MATCH_STARTING_TITLE_FOOTER("MATCH.STARTING.TITLE-FOOTER", DataType.STRING, ""),
    PARKOUR_CHECKPOINT("MATCH.PARKOUR.CHECKPOINT", DataType.STRING_LIST,
            "&a<player> finished checkpoint <checkpoint> in <time>!"),
    PARKOUR_END("MATCH.PARKOUR.END", DataType.STRING_LIST, "&a<player> finished the parkour in <time>!"),
    ROUND_STARTING("MATCH.ROUND.START.TIMER", DataType.STRING_LIST, "&fRound starting in &b<timer>&f..."),
    MATCH_START_TITLE_HEADER("MATCH.START.TITLE-HEADER", DataType.STRING, "&aFight!"),
    MATCH_START_TITLE_FOOTER("MATCH.START.TITLE-FOOTER", DataType.STRING, ""),
    MATCH_WINNER_TITLE_HEADER("MATCH.WINNER.TITLE-HEADER", DataType.STRING, "&aVICTORY!"),
    MATCH_WINNER_TITLE_FOOTER("MATCH.WINNER.TITLE-FOOTER", DataType.STRING, "&a<player> &fwon the match!"),

    MATCH_LOSER_TITLE_HEADER("MATCH.LOSER.TITLE-HEADER", DataType.STRING, "&cDEFEAT!"),
    MATCH_LOSER_TITLE_FOOTER("MATCH.LOSER.TITLE-FOOTER", DataType.STRING, "&a<player> &fwon the match!"),

    MATCH_COMMA("MATCH.COMMA", DataType.STRING, "&7, "),
    MATCH_END_DETAILS_SOLO("MATCH.END_DETAILS_MESSAGE.SOLO", DataType.STRING_LIST,
            " ",
            "&bMatch Results:",
            "&aWinner: &e<winner> &7| &cLoser: &e<loser>",
            " "),
    MATCH_END_DETAILS_DUEL("MATCH.END_DETAILS_MESSAGE.DUEL", DataType.STRING_LIST,
            " ",
            "&bMatch Results:",
            "&aWinner: &e<winner> &7| &cLoser: &e<loser>",
            " "),
    MATCH_END_DETAILS_TEAM("MATCH.END_DETAILS_MESSAGE.TEAM", DataType.STRING_LIST,
            " ",
            "&bMatch Results:",
            "&aWinners: &e<winners>",
            "&cLosers: &e<losers>",
            " "),
    MATCH_END_DETAILS_FFA("MATCH.END_DETAILS_MESSAGE.FFA", DataType.STRING_LIST,
            "",
            "&f&l<winner> &b&lwon the FFA match!",
            " "),
    MATCH_RESPAWN_TIMER("MATCH.RESPAWN_TIMER", DataType.STRING_LIST, "&fRespawning in &b<timer>&f..."),
    MATCH_RESPAWNED("MATCH.RESPAWNED", DataType.STRING_LIST, "&aRespawned!"),
    MATCH_RESPAWN_TITLE_HEADER("MATCH.RESPAWN_TITLE.HEADER", DataType.STRING, "&fRespawning in &b<timer>&f..."),
    MATCH_RESPAWN_TITLE_FOOTER("MATCH.RESPAWNED_TITLE_FOOTER", DataType.STRING, ""),
    MATCH_PLAY_AGAIN_ENABLED("MATCH.PLAY_AGAIN.ENABLED", DataType.BOOLEAN, "true"),
    MATCH_PLAY_AGAIN("MATCH.PLAY_AGAIN.MESSAGE", DataType.STRING, "&bDo you want to play again? &a(Click here)"),
    MATCH_PLAY_AGAIN_HOVER("MATCH.PLAY_AGAIN.HOVER", DataType.STRING, "&aClick to play again!"),
    MATCH_COMBO_MESSAGE_ENABLE("MATCH.COMBO_MESSAGE.ENABLE", DataType.BOOLEAN, "true"),
    MATCH_COMBO_MESSAGE_5("MATCH.COMBO_MESSAGE.5COMBO", DataType.STRING_LIST, "&a5 COMBO!"),
    MATCH_COMBO_MESSAGE_10("MATCH.COMBO_MESSAGE.10COMBO", DataType.STRING_LIST, "&e10 COMBO!"),
    MATCH_COMBO_MESSAGE_20("MATCH.COMBO_MESSAGE.20COMBO", DataType.STRING_LIST, "&c!!!20 COMBO!!!"),
    KIT_EDITOR_START("KIT_EDITOR.START", "This is sent when the player starts editing a kit.", DataType.STRING_LIST,
            "&bOpen your Inventory to edit layout!",
            "&bYou can use &f/kiteditor reset <kit> &bto reset the kit!"),
    KIT_EDITOR_STOP("KIT_EDITOR.STOP", "This is sent when the player finishes editing a kit.", DataType.STRING_LIST,
            "&aKit layout has been saved."),
    KIT_EDITOR_RESET("KIT_EDITOR.RESET", "This is sent when the player resets a kit.", DataType.STRING_LIST,
            "&aKit has been reset."),
    DUEL_REQUEST_RECEIVER("DUEL.SENT", DataType.STRING_LIST, " ",
            "&bDuel Request",
            " ",
            "&fSender: &a<sender>",
            "&fKit: &a<kit>",
            "&fArena: &a<arena>",
            "&fRounds: &b<rounds>",
            " ",
            "<accept> <deny>"),
    DUEL_REQUEST_SENDER("DUEL.SENDER", DataType.STRING_LIST, " ",
            "&bDuel Request Sent",
            " ",
            "&fReceiver: &a<receiver>",
            "&fKit: &a<kit>",
            "&fArena: &a<arena>",
            "&fRounds: &b<rounds>",
            " "),
    DUEL_DENY_SENDER("DUEL.SENDER_DENY", DataType.STRING_LIST, "&cDuel Denied."),
    DUEL_DENY_RECEIVER("DUEL.RECEIVER_DENY", DataType.STRING_LIST, "&cYour duel to &c<player> &cwas denied."),
    DUEL_ACCEPT("DUEL.ACCEPT", DataType.STRING, "&a&l(ACCEPT)"),
    DUEL_ACCEPT_HOVER("DUEL.ACCEPT_HOVER", DataType.STRING, "&aClick to accept duel request"),
    DUEL_DENY("DUEL.DENY", DataType.STRING, "&c&l(DENY)"),
    DUEL_DENY_HOVER("DUEL.DENY_HOVER", DataType.STRING, "&cClick to deny duel request"),
    DUEL_ALREADY_SENT("DUEL.ALREADY_SENT", DataType.STRING, "&cYou have already sent <player> a duel request."),
    DUEL_EXPIRED("DUEL.EXPIRED", DataType.STRING_LIST, "&cYour duel request to <player> has expired."),
    DUEL_NOT_ONLINE("DUEL.NOT_ONLINE", DataType.STRING_LIST, "&cPlayer isn't online!"),
    YOU_CANT_SEND_DUEL("DUEL.YOU_CANT_SEND_DUEL", DataType.STRING_LIST, "&cYou can't send duel requests right now!"),
    CANT_DUEL_SELF("DUEL.CANT_DUEL_SELF", DataType.STRING_LIST, "&cYou can't duel yourself!"),
    PLAYER_CANT_ACCEPT_DUEL("DUEL.PLAYER_CANT_ACCEPT_DUEL", DataType.STRING_LIST,
            "&cPlayer can't accept duel requests!"),
    YOU_DONT_HAVE_DUEL_REQUEST("DUEL.YOU_DONT_HAVE_DUEL_REQUEST", DataType.STRING_LIST,
            "&cYou don't have any duel request from this player!"),
    DUEL_REQUEST_COULDNT_BE_ACCEPTED("DUEL.REQUEST_COULDNT_BE_ACCEPTED", DataType.STRING_LIST,
            "&cDuel request couldn't be accepted!"),

    REMATCH_REQUEST_RECEIVER("REMATCH.SENT", DataType.STRING_LIST, " ",
            "&e&lRematch Request",
            "&eYou have received a rematch request from &a<sender>&e.",
            " ",
            "<accept> <deny>"),
    REMATCH_REQUEST_SENDER("REMATCH.SENDER", DataType.STRING_LIST, " ",
            "&e&lRematch Request Sent",
            "&eYou have sent a rematch request to &a<receiver>&e.",
            " "),
    REMATCH_ACCEPT("REMATCH.ACCEPT", DataType.STRING, "&a&l(ACCEPT)"),
    REMATCH_ACCEPT_HOVER("REMATCH.ACCEPT_HOVER", DataType.STRING, "&aClick to accept rematch request"),
    REMATCH_DENY("REMATCH.DENY", DataType.STRING, "&c&l(DENY)"),
    REMATCH_DENY_HOVER("REMATCH.DENY_HOVER", DataType.STRING, "&cClick to deny rematch request"),
    REMATCH_EXPIRED("REMATCH.EXPIRED", DataType.STRING_LIST, "&cYour rematch request to <player> has expired."),
    SPECTATE_START("MATCH.SPECTATE.START", DataType.STRING_LIST, "&b<player> &fstarted spectating match."),
    MATCH_FORFEIT("MATCH.FORFEIT", DataType.STRING_LIST, "&cSomeone rage quit"),
    SPECTATE_STOP("MATCH.SPECTATE.STOP", DataType.STRING_LIST, "&b<player> &fstopped spectating match."),
    SPECTATE_NOT_ALLOWED("MATCH.SPECTATE.SPECTATE_NOT_ALLOWED", DataType.STRING_LIST,
            "&c<player> has spectating disabled."),
    SPECTATE_LEAVE_IN_PARTY("MATCH.SPECTATE.SPECTATE_LEAVE_IN_PARTY", DataType.STRING,
            "&cYou are not allowed to leave spectating mode while being in a party!"),
    SPECTATE_PERMISSION_FLAG("MATCH.SPECTATE.NO_PERMISSION_FLAG", DataType.STRING_LIST,
            "&cYou don't have permission to use this flag!"),
    SPECTATE_SELF("MATCH.SPECTATE.SELF", DataType.STRING_LIST,
            "&cYou can't spectate yourself!"),
    SPECTATE_IN_MATCH("MATCH.SPECTATE.IN_MATCH", DataType.STRING_LIST,
            "&cYou can't spectate while in a match!"),
    SPECTATE_TARGET_NOT_IN_MATCH("MATCH.SPECTATE.TARGET_NOT_IN_MATCH", DataType.STRING_LIST,
            "&cPlayer isn't in a match!"),
    SPECTATE_STARTED("MATCH.SPECTATE.START", DataType.STRING_LIST,
            "&bYou are now spectating &f<player>&b."),
    SPECTATE_STARTED_SILENT("MATCH.SPECTATE.START.SILENT", DataType.STRING_LIST,
            "&bYou silently started spectating &f<player>&b."),
    ERROR_MESSAGE("ERROR_MESSAGE", DataType.STRING, "&c<error>"),
    JOIN_MESSAGE("JOIN_MESSAGE", DataType.STRING, "&8[&a+&8] &7<player> &7joined"),
    LEAVE_MESSAGE("LEAVE_MESSAGE", DataType.STRING, "&8[&c-&8] &7<player> &7left"),
    PARTY_CREATE("PARTY.CREATE", DataType.STRING_LIST, "&aCreated party!"),
    PARTY_DISBANDED("PARTY.DISABLED", DataType.STRING_LIST, "&cParty has been disbanded."),
    PARTY_INVITED("PARTY.INVITED", DataType.STRING_LIST, "&f<player> &bhas been invited to the party!"),
    PARTY_NOT_IN("PARTY.NOT_IN", DataType.STRING_LIST, "&cYou are not in a party."),
    PARTY_NOT_IN_PARTY("PARTY.NOT_IN_PARTY", DataType.STRING_LIST, "&c<player> isn't in a party."),
    PARTY_NOT_LEADER("PARTY.NOT_LEADER", DataType.STRING_LIST, "&cPlayer <player> isn't a leader of a party."),
    PARTY_PRIVATE("PARTY.PRIVATE", DataType.STRING_LIST, "&cThis party is private."),
    PARTY_NOT_IN_SAME_PARTY("PARTY.NOT_IN_SAME_PARTY", DataType.STRING_LIST, "&c<player> isn't in your party."),
    PARTY_JOINED("PARTY.JOINED", DataType.STRING_LIST, "&f<player> &bjoined the party!"),
    PARTY_INVITATION("PARTY.INVITATION", DataType.STRING_LIST,
            "&bYou have been invited to &f<leader>'s &bparty <accept>"),
    PARTY_INVITE_OWN("PARTY.INVITE_OWN", DataType.STRING_LIST, "&cYou can't invite yourself to the party."),
    PARTY_TRANSFER_OWN("PARTY.TRANSFER", DataType.STRING_LIST, "&cYou can't transfer a party to yourself."),
    PARTY_ACCEPT("PARTY.ACCEPT", DataType.STRING, "&a&l(ACCEPT)"),
    PARTY_ACCEPT_HOVER("PARTY.ACCEPT_HOVER", DataType.STRING, "&aClick to accept party request"),
    PARTY_NO_PERMISSION("PARTY.NO_PERMISSION", DataType.STRING_LIST, "&cYou do not have permission to do this."),
    PARTY_DISABLED("PARTY.DISABLED", DataType.STRING_LIST, "&c<player> has party requests disabled!"),
    PARTY_ALREADY_IN("PARTY.ALREADY_IN", DataType.STRING_LIST, "&cYou are already in a party."),
    PARTY_ALREADY_SENT("PARTY.ALREADY_SENT", DataType.STRING_LIST, "&cYou have already sent <player> a party request."),
    PARTY_ALREADY_PARTY("PARTY.ALREADY_IN_PARTY", DataType.STRING_LIST, "&c<player> is already in a party."),
    PARTY_TRANSFER("PARTY.TRANSFER.MEMBERS", DataType.STRING_LIST,
            "&f<leader> &btransferred the party to &f<target>&b."),
    PARTY_ADVERTISE_MESSAGE("PARTY.ADVERTISE.MESSAGE", DataType.STRING_LIST,
            "&f<leader> &6wants you in their party! <join>"),
    PARTY_ADVERTISE_JOIN("PARTY.ADVERTISE.JOIN", DataType.STRING, "&a(Join)"),
    PARTY_ADVERTISE_JOIN_HOVER("PARTY.ADVERTISE.JOIN_HOVER", DataType.STRING, "&aClick to join &f<leader>&a's party."),
    PARTY_KICK("PARTY.KICK", DataType.STRING_LIST, "&f<player> &bhas been kicked from the party."),
    PARTY_CANNOT_CREATE("PARTY.CANNOT_CREATE", DataType.STRING_LIST, "&cYou can only create a party while in lobby!"),
    PARTY_LEFT("PARTY.LEFT", DataType.STRING_LIST, "&f<player> &bhas left the party."),
    PARTY_INFO("PARTY.INFO", DataType.STRING_LIST,
            " ",
            "&7&m------------------------------------------------",
            "&fPrivacy: &b<privacy>",
            "&fLeader: &b<leader>",
            "&fSize: &b<size>/<party-max>",
            "&7&m------------------------------------------------"),
    PARTY_MAX_SIZE("PARTY.MAX_SIZE_REACHED", DataType.STRING_LIST, "&cYou have reached max party size"),
    PARTY_NOT_ENOUGH_MEMBERS("PARTY.NOT_ENOUGH_MEMBERS", DataType.STRING_LIST,
            "&cYou need at least 2 players to start a party event."),
    PARTY_HELP("PARTY.HELP", "Message sent on /party help", DataType.STRING_LIST,
            "&bParty Help",
            " ",
            "&b/party create",
            "&b/party invite <player>",
            "&b/party disband",
            "&b/party leave",
            "&b/party kick <player>",
            "&b/party join <player>",
            "&b/party transfer <player>",
            "&b/party advertise",
            " "),
    PARTY_EXPIRED("PARTY.EXPIRED", DataType.STRING_LIST, "&cYour party request to &c<player> &chas expired."),
    START_FOLLOW("FOLLOW.STARTED", DataType.STRING_LIST, "&bStarted following &f<player>"),
    STOP_FOLLOWING("FOLLOW.STOPPED", DataType.STRING_LIST, "&cStopped following <player>"),
    NOT_ONLINE("NOT_ONLINE", DataType.STRING_LIST, "&c<player> isn't online!"),
    CANT_BREAK_OWN_BED("CANT.BREAK_OWN_BED", DataType.STRING_LIST, "&cYou can't break your own bed!"),
    BED_BREAK_TITLE("BEDWARS.OWN_BREAK.TITLE", DataType.STRING, "&cBED DESTROYED!"),
    BED_BREAK_FOOTER("BEDWARS.OWN_BREAK.FOOTER", DataType.STRING, "&fYou will no longer respawn!"),
    BLUE_BED_BROKEN_MESSAGE("BEDWARS.BLUE_BREAK.MESSAGE", DataType.STRING, "&9Blue Bed &7was broken by <player>"),
    RED_BED_BROKEN_MESSAGE("BEDWARS.RED_BREAK.MESSAGE", DataType.STRING, "&cRed Bed &7was broken by <player>"),
    MATCH_ENDERPEARL_COOLDOWN_ON_GOING("MATCH.ENDERPEARL_COOLDOWN.ON_GOING", DataType.STRING_LIST,
            "&cEnderpearl cooldown&7: &e<time>s"),
    FFA_KILLSTREAK_ANNOUNCE_RULES("FFA.KILLSTREAK_RULES",
            "Rules: 5,10,15 (exact); >5 (above); <5 (below); +5 (every 5 kills). Multiple rules separated by ,",
            DataType.STRING, "+5"),
    FFA_KILL_ANNOUNCE("FFA.KILL_ANNOUNCE", DataType.STRING_LIST,
            "&b⚔ &b<player> &fwas killed by &b<killer>",
            "&b⚔ &b<player> &fwas 360 no-scoped by &b<killer>",
            "&b⚔ &b<player> &fwas diddled by &b<killer>",
            "&b⚔ &b<player> &fwas sent a j*b application by &b<killer>"),
    FFA_KILLSTREAK_ANNOUNCE_ENABLED("FFA.KILLSTREAK_ANNOUNCE.ENABLED", DataType.BOOLEAN, "false"),
    IN_MATCH_BLOCKED_COMMAND_MESSAGE("IN_MATCH_BLOCKED_COMMAND", DataType.STRING_LIST, "&cThis command is blocked while in a match!"),
    FFA_KILLSTREAK_ANNOUNCE_MESSAGE("FFA.KILLSTREAK_ANNOUNCE.MESSAGE", DataType.STRING_LIST,
            " ", "&b<player> &fis now on a &b&l<killstreak> KILLSTREAK!", " ");

    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    MessagesLocale(String path, @Nullable String comment, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = comment;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    MessagesLocale(String path, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = null;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getMessagesConfig();
    }

    @Override
    public String getHeader() {
        return "Replace with NONE to disable";
    }

    public void send(Player player, Replacement... replacements) {
        final UUID playerUUID = player.getUniqueId();
        if (dataType.equals(DataType.STRING_LIST)) {
            for (String message : getStringList()) {
                if (message.equals("NONE"))
                    continue;
                PlayerUtil.sendMessage(playerUUID, CC.returnMessage(player, message, replacements));
            }
        } else if (dataType.equals(DataType.STRING)) {
            if (getString().equals("NONE"))
                return;
            PlayerUtil.sendMessage(playerUUID, CC.returnMessage(player, getString(), replacements));
        }
    }

    public void send(UUID playerUUID, Replacement... replacements) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;
        if (dataType.equals(DataType.STRING_LIST)) {
            for (String message : getStringList()) {
                if (message.equals("NONE"))
                    continue;
                PlayerUtil.sendMessage(playerUUID, CC.returnMessage(player, message, replacements));
            }
        } else if (dataType.equals(DataType.STRING)) {
            if (getString().equals("NONE"))
                return;
            PlayerUtil.sendMessage(playerUUID, CC.returnMessage(player, getString(), replacements));
        }
    }
}