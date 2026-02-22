package dev.lrxh.neptune.feature.hotbar.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.divisions.menu.DivisionsMenu;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.feature.leaderboard.menu.LeaderboardMenu;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.menu.PartySettingsMenu;
import dev.lrxh.neptune.feature.party.menu.buttons.events.PartyDuelMenu;
import dev.lrxh.neptune.feature.party.menu.buttons.events.PartyEventsMenu;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.feature.queue.menu.QueueMenu;
import dev.lrxh.neptune.feature.settings.menu.SettingsMenu;
import dev.lrxh.neptune.game.duel.DuelRequest;
import dev.lrxh.neptune.game.kit.menu.StatsMenu;
import dev.lrxh.neptune.game.kit.menu.editor.KitEditorMenu;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.menu.MatchListMenu;
import dev.lrxh.neptune.game.match.menu.MatchSpectateTeleportMenu;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.PlayerUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public enum ItemAction {

    UNRANKED() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player);

            if (profile.getState().equals(ProfileState.IN_PARTY)) {
                MessagesLocale.CANT_DO_THIS_NOW.send(player.getUniqueId());
                return;
            }
            new QueueMenu().open(player);
        }
    },
    QUEUE_LEAVE() {
        @Override
        public void execute(Player player) {
            API.getProfile(player.getUniqueId()).setState(ProfileState.IN_LOBBY);
            QueueEntry queueEntry = QueueService.get().remove(player.getUniqueId());
            MessagesLocale.QUEUE_LEAVE.send(player.getUniqueId(),
                    Placeholder.parsed("kit", queueEntry.getKit().getDisplayName()));
        }
    },
    KIT_EDITOR() {
        @Override
        public void execute(Player player) {
            new KitEditorMenu().open(player);
        }
    },
    STATS() {
        @Override
        public void execute(Player player) {
            new StatsMenu(player.getName()).open(player);
        }
    },
    SPECTATE_MENU {
        @Override
        public void execute(Player player) {
            new MatchListMenu().open(player);
        }
    },
    LEADERBOARDS() {
        @Override
        public void execute(Player player) {
            new LeaderboardMenu(LeaderboardType.KILLS).open(player);
        }
    },
    PARTY_CREATE() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player);
            profile.createParty();
        }
    },
    PARTY_INFO() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player);
            if (profile.getGameData().getParty() == null) {
                MessagesLocale.PARTY_NOT_IN.send(player);
                return;
            }
            Party party = profile.getGameData().getParty();
            MessagesLocale.PARTY_INFO.send(player.getUniqueId(), TagResolver.resolver(
                    Placeholder.unparsed("leader", party.getLeaderName()),
                    Placeholder.unparsed("privacy", party.isOpen() ? MessagesLocale.PARTY_PRIVACY_OPEN.getString() : MessagesLocale.PARTY_PRIVACY_CLOSED.getString()),
                    Placeholder.unparsed("max", String.valueOf(party.getMaxUsers())),
                    Placeholder.unparsed("members", party.getUserNames()),
                    Placeholder.unparsed("size", String.valueOf(party.getUsers().size()))));
        }
    },
    PARTY_DISBAND() {
        @Override
        public void execute(Player player) {
            API.getProfile(player.getUniqueId()).disband();
        }
    },
    PARTY_EVENTS() {
        @Override
        public void execute(Player player) {
            Party party = API.getProfile(player.getUniqueId()).getGameData().getParty();
            if (!party.getLeader().equals(player.getUniqueId())) {
                MessagesLocale.PARTY_NO_PERMISSION.send(player.getUniqueId());
                return;
            }
            if (party.getUsers().size() < 2) {
                MessagesLocale.PARTY_NOT_ENOUGH_MEMBERS.send(player.getUniqueId());
                return;
            }

            new PartyEventsMenu(API.getProfile(player.getUniqueId()).getGameData().getParty()).open(player);
        }
    },
    PARTY_DUEL() {
        @Override
        public void execute(Player player) {
            Party party = API.getProfile(player.getUniqueId()).getGameData().getParty();
            if (!party.getLeader().equals(player.getUniqueId())) {
                MessagesLocale.PARTY_NO_PERMISSION.send(player.getUniqueId());
                return;
            }
            new PartyDuelMenu(API.getProfile(player.getUniqueId()).getGameData().getParty()).open(player);
        }
    },
    PARTY_SETTINGS() {
        @Override
        public void execute(Player player) {
            Party party = API.getProfile(player.getUniqueId()).getGameData().getParty();
            if (!party.getLeader().equals(player.getUniqueId())) {
                MessagesLocale.PARTY_NO_PERMISSION.send(player.getUniqueId());
                return;
            }
            new PartySettingsMenu(party).open(player);
        }
    },
    DIVISIONS() {
        @Override
        public void execute(Player player) {
            new DivisionsMenu().open(player);
        }
    },
    PLAY_AGAIN() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player);
            Match match = profile.getMatch();
            if (match == null)
                return;
            Participant participant = match.getParticipant(player);
            participant.setDisconnected(true);
            PlayerUtil.reset(participant.getPlayer());
            PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
            profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
            profile.setMatch(null);

            QueueService.get().add(new QueueEntry(match.getKit(), player.getUniqueId()), true);
        }
    },
    REMATCH() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player);
            if (profile == null)
                return;
            SoloFightMatch match = (SoloFightMatch) profile.getMatch();
            if (match == null)
                return;
            match.getKit().getRandomArena().thenAccept(arena -> {
                if (arena == null) {
                    MessagesLocale.QUEUE_NO_ARENAS.send(player);
                    return;
                }
                DuelRequest duelRequest = new DuelRequest(profile.getPlayerUUID(), match.getKit(), arena, false,
                        match.getRounds());
                Player opponent = match.getParticipant(player).getOpponent().getPlayer();
                if (opponent == null)
                    return;
                Profile opponentProfile = API.getProfile(opponent);
                if (opponentProfile == null)
                    return;
                opponentProfile.sendRematch(duelRequest);
            });
        }
    },
    SETTINGS() {
        @Override
        public void execute(Player player) {
            new SettingsMenu().open(player);
        }
    },
    SPECTATOR_TELEPORT() {
        @Override
        public void execute(Player player) {
            new MatchSpectateTeleportMenu().open(player);
        }
    },
    SPECTATOR_LEAVE() {
        @Override
        public void execute(Player player) {
            player.chat("/leave");
        }
    };

    public abstract void execute(Player player);
}
