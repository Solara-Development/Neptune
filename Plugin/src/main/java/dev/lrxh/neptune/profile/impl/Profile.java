package dev.lrxh.neptune.profile.impl;

import dev.lrxh.api.profile.IProfile;
import dev.lrxh.api.profile.IProfileState;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.cosmetics.CosmeticService;
import dev.lrxh.neptune.feature.cosmetics.impl.KillEffect;
import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.PartyService;
import dev.lrxh.neptune.game.arena.procedure.ArenaProcedure;
import dev.lrxh.neptune.game.duel.DuelRequest;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.procedure.KitProcedure;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.profile.data.*;
import dev.lrxh.neptune.providers.clickable.ClickableComponent;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.database.DatabaseService;
import dev.lrxh.neptune.providers.database.impl.DataDocument;
import dev.lrxh.neptune.utils.Cooldown;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
public class Profile implements IProfile {
    private final UUID playerUUID;
    private Map<String, Cooldown> cooldowns;
    private String username;
    private ProfileState state; // for main plugin, if this was set to ProfileState.IN_CUSTOM, it will use customState instead
    private String customState;
    private Neptune plugin;
    private GameData gameData;
    private SettingData settingData;
    private Visibility visibility;
    private ArenaProcedure arenaProcedure;
    private KitProcedure kitProcedure;
    private boolean fake;

    public Profile(String name, UUID uuid, Neptune plugin, boolean fake) {
        this.plugin = plugin;
        this.username = name;
        this.playerUUID = uuid;
        this.state = ProfileState.IN_LOBBY;
        this.gameData = new GameData(this);
        this.settingData = new SettingData(plugin);
        this.visibility = new Visibility(playerUUID);
        this.arenaProcedure = new ArenaProcedure();
        this.kitProcedure = new KitProcedure();
        this.fake = fake;
        this.cooldowns = new HashMap<>();

        load();
    }


    public void handleVisibility() {
        visibility.handle();
    }

    public void setState(ProfileState profileState) {
        state = profileState;
        customState = "";
        handleVisibility();
        HotbarService.get().giveItems(getPlayer());
    }

    @Override
    public void setState(String customState) {
        this.customState = "";
        switch (customState) {
            case "neptune:in_lobby" -> this.state = ProfileState.IN_LOBBY;
            case "neptune:in_game" -> this.state = ProfileState.IN_GAME;
            case "neptune:in_kiteditor" -> this.state = ProfileState.IN_KIT_EDITOR;
            case "neptune:in_party" -> this.state = ProfileState.IN_PARTY;
            case "neptune:spectating" -> this.state = ProfileState.IN_SPECTATOR;
            case "neptune:in_queue" -> this.state = ProfileState.IN_QUEUE;
            default -> {
                this.state = ProfileState.IN_CUSTOM;
                this.customState = customState;
            }
        }
        handleVisibility();
    }

    @Override
    public String getProfileState() {
        return this.customState;
    }

    @Override
    public void toLobby() {
        setState(ProfileState.IN_LOBBY);
        PlayerUtil.teleportToSpawn(playerUUID);
        if (getMatch() != null) {
            getMatch().onDeath(getMatch().getParticipant(playerUUID));
        }
        getPlayer().setHealth(20);
    }

    public boolean hasState(IProfileState state) {
        return this.state.equals(state);
    }

    @Override
    public boolean hasState(String customState) {
        if (this.state != ProfileState.IN_CUSTOM) {
            return switch (customState) {
                case "neptune:in_lobby" -> this.state == ProfileState.IN_LOBBY;
                case "neptune:in_game" -> this.state == ProfileState.IN_GAME;
                case "neptune:in_kiteditor" -> this.state == ProfileState.IN_KIT_EDITOR;
                case "neptune:in_party" -> this.state == ProfileState.IN_PARTY;
                case "neptune:spectating" -> this.state == ProfileState.IN_SPECTATOR;
                case "neptune:in_queue" -> this.state == ProfileState.IN_QUEUE;
                default -> false;
            };
        }
        return this.customState.equals(customState);
    }


    public boolean hasState(ProfileState... profileStates) {
        for (ProfileState profileState : profileStates) {
            if (profileState.equals(state)) {
                return true;
            }
        }
        return false;
    }

    public void addCooldown(String name, int millis) {
        Cooldown cooldown = new Cooldown(millis);
        cooldowns.put(name, cooldown);
        cooldown.start();
    }

    public void addCooldown(String name, int millis, NeptuneRunnable runnable) {
        Cooldown cooldown = new Cooldown(millis, runnable);
        cooldowns.put(name, cooldown);
        cooldown.start();
    }


    public boolean hasCooldownEnded(String name) {
        if (!cooldowns.containsKey(name)) {
            return true;
        }

        if (cooldowns.get(name).isExpired()) {
            cooldowns.remove(name);
            return true;
        }

        return false;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public void load() {
        DatabaseService.get().getDatabase().getUserData(playerUUID).thenAccept(dataDocument -> {
            if (dataDocument == null) {
                save();
            }

            if (dataDocument == null) return;

            gameData.setMatchHistories(gameData.deserializeHistory(dataDocument.getList("history", new ArrayList<>())));

            DataDocument kitStatistics = dataDocument.getDataDocument("kitData");
            DataDocument settings = dataDocument.getDataDocument("settings");

            for (Kit kit : KitService.get().kits) {
                DataDocument kitDocument = kitStatistics.getDataDocument(kit.getName());
                if (kitDocument == null) return;
                KitData profileKitData = gameData.get(kit);
                profileKitData.setCurrentStreak(kitDocument.getInteger("WIN_STREAK_CURRENT", 0));
                profileKitData.setKills(kitDocument.getInteger("WINS", 0));
                profileKitData.setElo(kitDocument.getInteger("ELO", 0));
                profileKitData.setDivision(DivisionService.get().getDivisionByElo(profileKitData.getElo()));
                profileKitData.setDeaths(kitDocument.getInteger("LOSSES", 0));
                profileKitData.setBestStreak(kitDocument.getInteger("WIN_STREAK_BEST", 0));
                profileKitData.setKitLoadout(Objects.equals(kitDocument.getString("kit"), "") ? kit.getItems() : ItemUtils.deserialize(kitDocument.getString("kit")));

                // Load persistent custom data for each kit
                DataDocument customPersistentData = kitDocument.getDataDocument("customPersistentData");
                if (customPersistentData != null) {
                    for (String key : customPersistentData.data.keySet()) {
                        profileKitData.setPersistentData(key, customPersistentData.data.get(key));
                    }
                }

                profileKitData.updateDivision();
            }

            gameData.setLastPlayedKit(kitStatistics.getString("lastPlayedKit", ""));

            settingData.setPlayerVisibility(settings.getBoolean("showPlayers", true));
            settingData.setAllowSpectators(settings.getBoolean("allowSpectators", true));
            settingData.setAllowDuels(settings.getBoolean("allowDuels", true));
            settingData.setAllowParty(settings.getBoolean("allowParty", true));
            settingData.setMaxPing(settings.getInteger("maxPing", 350));
            settingData.setKillEffect(KillEffect.valueOf(settings.getString("killEffect", "NONE")));
            settingData.setMenuSound(settings.getBoolean("menuSound", false));
            settingData.setKillMessagePackage(CosmeticService.get().getDeathMessagePackage(settings.getString("deathMessagePackage")));

            // Load global persistent custom data
            DataDocument globalCustomPersistentData = dataDocument.getDataDocument("customPersistentData");
            if (globalCustomPersistentData != null) {
                for (String key : globalCustomPersistentData.data.keySet()) {
                    gameData.setPersistentData(key, globalCustomPersistentData.data.get(key));
                }
            }

            this.gameData.getGlobalStats().update();
        });
    }

    public void save() {
        DataDocument dataDocument = new DataDocument();
        dataDocument.put("uuid", playerUUID.toString());

        dataDocument.put("username", username);

        DataDocument kitStatsDoc = new DataDocument();

        dataDocument.put("history", gameData.serializeHistory());

        for (Kit kit : KitService.get().kits) {
            DataDocument kitStatisticsDocument = new DataDocument();
            KitData entry = gameData.get(kit);
            kitStatisticsDocument.put("WIN_STREAK_CURRENT", entry.getCurrentStreak());
            kitStatisticsDocument.put("WINS", entry.getKills());
            kitStatisticsDocument.put("ELO", entry.getElo());
            kitStatisticsDocument.put("LOSSES", entry.getDeaths());
            kitStatisticsDocument.put("WIN_STREAK_BEST", entry.getBestStreak());
            kitStatisticsDocument.put("kit", entry.getKitLoadout() == null || entry.getKitLoadout().isEmpty() ? "" : ItemUtils.serialize(entry.getKitLoadout()));
            entry.updateDivision();
            kitStatsDoc.put(kit.getName(), kitStatisticsDocument);
            DataDocument customPersistentData = new DataDocument();
            for (String data : entry.getPersistentData().keySet()) {
                customPersistentData.put(data, entry.getPersistentData().get(data));
            }
            kitStatisticsDocument.put("customPersistentData", customPersistentData);
        }

        kitStatsDoc.put("lastPlayedKit", gameData.getLastPlayedKit());

        dataDocument.put("kitData", kitStatsDoc);

        DataDocument settingsDoc = new DataDocument();

        settingsDoc.put("showPlayers", settingData.isPlayerVisibility());
        settingsDoc.put("allowSpectators", settingData.isAllowSpectators());
        settingsDoc.put("allowDuels", settingData.isAllowDuels());
        settingsDoc.put("allowParty", settingData.isAllowParty());
        settingsDoc.put("maxPing", settingData.getMaxPing());
        settingsDoc.put("killEffect", settingData.getKillEffect().toString());
        settingsDoc.put("menuSound", settingData.isMenuSound());
        settingsDoc.put("deathMessagePackage", settingData.getKillMessagePackage().getName());

        dataDocument.put("settings", settingsDoc);

        DataDocument customPersistentData = new DataDocument();

        for (String data : gameData.getPersistentData().keySet()) {
            customPersistentData.put(data, gameData.getPersistentData().get(data));
        }

        dataDocument.put("customPersistentData", customPersistentData);

        DatabaseService.get().getDatabase().replace(playerUUID, dataDocument);
    }

    public void sendDuel(DuelRequest duelRequest) {
        UUID senderUUID = duelRequest.getSender();

        Player sender = Bukkit.getPlayer(senderUUID);
        if (sender == null) return;

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        MessagesLocale.DUEL_REQUEST_SENDER.send(sender.getUniqueId(),
                new Replacement("<receiver>", username),
                new Replacement("<kit>", duelRequest.getKit().getDisplayName()),
                new Replacement("<rounds>", String.valueOf(duelRequest.getRounds())),
                new Replacement("<arena>", duelRequest.getArena().getDisplayName()));

        gameData.addRequest(duelRequest, senderUUID, ignore -> MessagesLocale.DUEL_EXPIRED.send(senderUUID, new Replacement("<player>", player.getName())));

        TextComponent accept =
                new ClickableComponent(MessagesLocale.DUEL_ACCEPT.getString(), "/duel accept-uuid " + duelRequest.getSender().toString(), MessagesLocale.DUEL_ACCEPT_HOVER.getString()).build();

        TextComponent deny =
                new ClickableComponent(MessagesLocale.DUEL_DENY.getString(), "/duel deny-uuid " + duelRequest.getSender().toString(), MessagesLocale.DUEL_DENY_HOVER.getString()).build();

        MessagesLocale.DUEL_REQUEST_RECEIVER.send(playerUUID,
                new Replacement("<accept>", accept),
                new Replacement("<deny>", deny),
                new Replacement("<kit>", duelRequest.getKit().getDisplayName()),
                new Replacement("<arena>", duelRequest.getArena().getDisplayName()),
                new Replacement("<rounds>", String.valueOf(duelRequest.getRounds())),
                new Replacement("<sender>", sender.getName()));
    }

    public void sendRematch(DuelRequest duelRequest) {
        UUID senderUUID = duelRequest.getSender();

        Player sender = Bukkit.getPlayer(senderUUID);
        if (sender == null) return;

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        MessagesLocale.REMATCH_REQUEST_SENDER.send(sender.getUniqueId(),
                new Replacement("<receiver>", username),
                new Replacement("<arena>", duelRequest.getArena().getDisplayName()));

        gameData.addRequest(duelRequest, senderUUID, ignore -> MessagesLocale.REMATCH_EXPIRED.send(senderUUID, new Replacement("<player>", player.getName())));

        TextComponent accept =
                new ClickableComponent(MessagesLocale.REMATCH_ACCEPT.getString(), "/duel accept-uuid " + duelRequest.getSender().toString(), MessagesLocale.REMATCH_ACCEPT_HOVER.getString()).build();

        TextComponent deny =
                new ClickableComponent(MessagesLocale.REMATCH_DENY.getString(), "/duel accept-uuid " + duelRequest.getSender().toString(), MessagesLocale.REMATCH_DENY_HOVER.getString()).build();

        MessagesLocale.REMATCH_REQUEST_RECEIVER.send(playerUUID,
                new Replacement("<accept>", accept),
                new Replacement("<deny>", deny),
                new Replacement("<kit>", duelRequest.getKit().getDisplayName()),
                new Replacement("<arena>", duelRequest.getArena().getDisplayName()),
                new Replacement("<rounds>", String.valueOf(duelRequest.getRounds())),
                new Replacement("<sender>", sender.getName()));
    }

    public Party createParty() {
        if (gameData.getParty() != null) {
            MessagesLocale.PARTY_ALREADY_IN.send(playerUUID);
            return null;
        }
        Party party = new Party(playerUUID, plugin);
        PartyService.get().addParty(party);
        API.getProfile(playerUUID).getGameData().setParty(party);
        MessagesLocale.PARTY_CREATE.send(playerUUID);
        return party;
    }

    public void disband() {
        Party party = gameData.getParty();

        if (party == null) {
            return;
        }

        party.broadcast(MessagesLocale.PARTY_LEFT,
                new Replacement("<player>", username));
        party.remove(playerUUID);
    }

    public void acceptDuel(UUID senderUUID) {
        ((DuelRequest) gameData.getRequests().get(senderUUID)).start(playerUUID);
        gameData.removeRequest(senderUUID);
    }

    public Match getMatch() {
        return gameData.getMatch();
    }


    public void setMatch(Match match) {
        gameData.setMatch(match);
    }

}
