package dev.lrxh.neptune.feature.party;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.party.impl.PartyRequest;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.ClickableComponent;
import dev.lrxh.neptune.providers.clickable.Replacement;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@Setter
public class Party {
    private final HashSet<UUID> users;
    private final boolean duelRequest;
    private final Neptune plugin;
    private UUID leader;
    private boolean open;
    private int maxUsers;

    public Party(UUID leader, int max, Neptune plugin) {
        this.leader = leader;
        this.users = new HashSet<>();
        this.users.add(leader);
        this.open = false;
        this.maxUsers = max;
        this.duelRequest = true;
        this.plugin = plugin;

        setupPlayer(leader);
    }

    public String getLeaderName() {
        Player player = Bukkit.getPlayer(leader);
        if (player == null) {
            users.remove(leader);
            return "";
        }

        return player.getName();
    }

    public boolean isLeader(UUID playerUUID) {
        return leader == playerUUID;
    }

    public void invite(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;

        TextComponent accept = new ClickableComponent(
                MessagesLocale.PARTY_ACCEPT.getString().replace("<leader>", getLeaderName()),
                "/party accept " + leader, MessagesLocale.PARTY_ACCEPT_HOVER.getString()).build();

        MessagesLocale.PARTY_INVITATION.send(playerUUID,
                new Replacement("<accept>", accept),
                new Replacement("<leader>", getLeaderName()));

        Profile profile = API.getProfile(playerUUID);
        profile.getGameData().addRequest(new PartyRequest(leader, this), leader,
                ignore -> MessagesLocale.PARTY_EXPIRED.send(leader, new Replacement("<player>", player.getName())));
    }

    public void accept(UUID playerUUID) {
        if (users.size() + 1 > maxUsers)
            return;
        setupPlayer(playerUUID);
    }

    private void setupPlayer(UUID playerUUID) {
        Player invitedPlayer = Bukkit.getPlayer(playerUUID);
        if (invitedPlayer == null)
            return;
        users.add(playerUUID);
        Profile profile = API.getProfile(playerUUID);
        profile.getGameData().setParty(this);
        profile.setState(ProfileState.IN_PARTY);
        if (playerUUID != leader) {
            broadcast(MessagesLocale.PARTY_JOINED, new Replacement("<player>", invitedPlayer.getName()));
        }
        API.getProfile(playerUUID).getGameData().removeRequest(leader);
    }

    public void kick(UUID playerUUID) {
        broadcast(MessagesLocale.PARTY_KICK, new Replacement("<player>", getLeaderName()));
        remove(playerUUID);
    }

    public void remove(UUID playerUUID) {
        if (leader == playerUUID) {
            disband();
            return;
        }

        Profile profile = API.getProfile(playerUUID);
        users.remove(playerUUID);
        profile.setState(ProfileState.IN_LOBBY);
        profile.getGameData().setParty(null);
    }

    public void disband() {
        broadcast(MessagesLocale.PARTY_DISBANDED);

        forEachMemberAsUUID(uuid -> {
            Profile profile = API.getProfile(uuid);
            profile.getGameData().setParty(null);
            if (profile.getState().equals(ProfileState.IN_PARTY))
                profile.setState(ProfileState.IN_LOBBY);
        });

        PartyService.get().removeParty(this);
    }

    public void broadcast(MessagesLocale messagesLocale, Replacement... replacements) {
        forEachMemberAsUUID(uuid -> messagesLocale.send(uuid, replacements));
    }

    public String getUserNames() {
        StringBuilder playerNames = new StringBuilder();
        forEachMemberAsPlayer(player -> {
            if (!playerNames.isEmpty()) {
                playerNames.append(MessagesLocale.MATCH_COMMA.getString());
            }

            playerNames.append(player.getName());
        });
        return playerNames.toString();
    }

    public void forEachMemberAsPlayer(Consumer<Player> action) {
        for (UUID user : users) {
            Player player = Bukkit.getPlayer(user);
            if (player != null) {
                action.accept(player);
            }
        }
    }

    public void forEachMemberAsUUID(Consumer<UUID> action) {
        for (UUID user : users) {
            action.accept(user);
        }
    }

    public void transfer(Player player, Player target) {
        this.setLeader(target.getUniqueId());
        this.broadcast(MessagesLocale.PARTY_TRANSFER, new Replacement("<leader>", player.getName()),
                new Replacement("<target>", target.getName()));
    }

    public boolean advertise() {
        Profile leaderProfile = API.getProfile(leader);

        if (leaderProfile.hasCooldownEnded("party_advertise")) {
            leaderProfile.addCooldown("party_advertise", 300_000);

            setOpen(true);
            for (Profile profile : ProfileService.get().profiles.values()) {
                TextComponent join = new ClickableComponent(
                        MessagesLocale.PARTY_ADVERTISE_JOIN.getString(),
                        "/party join " + getLeaderName(),
                        MessagesLocale.PARTY_ADVERTISE_JOIN_HOVER.getString().replaceAll("<leader>", getLeaderName()))
                        .build();

                MessagesLocale.PARTY_ADVERTISE_MESSAGE.send(
                        profile.getPlayerUUID(),
                        new Replacement("<join>", join),
                        new Replacement("<leader>", getLeaderName()));
            }

            return true;
        }

        return false;
    }

}
