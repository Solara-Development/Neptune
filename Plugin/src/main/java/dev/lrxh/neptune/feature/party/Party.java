package dev.lrxh.neptune.feature.party;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.party.impl.PartyRequest;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

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

        setupPlayer(leader, false);
    }

    public Player getLeaderPlayer() {
        Player player = Bukkit.getPlayer(leader);
        if (player == null) {
            users.remove(leader);
            return null;
        }
        return player;
    }
    public String getLeaderName() {
        Player player = getLeaderPlayer();
        if (player == null) return "";
        return player.getName();
    }

    public boolean isLeader(UUID playerUUID) {
        return leader == playerUUID;
    }

    public void invite(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;

        MessagesLocale.PARTY_INVITATION.send(playerUUID, TagResolver.resolver(
                TagResolver.resolver("accept", Tag.styling(ClickEvent.runCommand("/party accept " + getLeader()))),
                Placeholder.unparsed("leader", getLeaderName()),
                Placeholder.unparsed("party-max", String.valueOf(getMaxUsers())),
                Placeholder.unparsed("party-size", String.valueOf(getUsers().size()))
        ));

        Profile profile = API.getProfile(playerUUID);
        profile.getGameData().addRequest(new PartyRequest(leader, this), leader,
                ignore -> MessagesLocale.PARTY_EXPIRED.send(leader, Placeholder.unparsed("player", player.getName())));
    }

    public void accept(UUID playerUUID, boolean ad) {
        if (users.size() + 1 > maxUsers)
            return;
        setupPlayer(playerUUID, ad);
    }

    private void setupPlayer(UUID playerUUID, boolean ad) {
        Player invitedPlayer = Bukkit.getPlayer(playerUUID);
        if (invitedPlayer == null)
            return;
        users.add(playerUUID);
        Profile profile = API.getProfile(playerUUID);
        profile.getGameData().setParty(this);
        profile.setState(ProfileState.IN_PARTY);
        if (playerUUID != leader) {
            broadcast(ad ? MessagesLocale.PARTY_JOINED_FROM_ADVERTISEMENT : MessagesLocale.PARTY_JOINED, Placeholder.unparsed("player", invitedPlayer.getName()));
        }
        API.getProfile(playerUUID).getGameData().removeRequest(leader);
    }

    public void kick(UUID playerUUID) {
        broadcast(MessagesLocale.PARTY_KICK, Placeholder.unparsed("player", getLeaderName()));
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

    public void broadcast(MessagesLocale messagesLocale) {
        forEachMemberAsUUID(messagesLocale::send);
    }
    public void broadcast(MessagesLocale messagesLocale, TagResolver resolver) {
        forEachMemberAsUUID(uuid -> messagesLocale.send(uuid, resolver));
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
        this.broadcast(MessagesLocale.PARTY_TRANSFER, TagResolver.resolver(
            Placeholder.unparsed("leader", player.getName()),
            Placeholder.unparsed("target", target.getName())
        ));
    }

    public boolean advertise() {
        Profile leaderProfile = API.getProfile(leader);

        if (leaderProfile.hasCooldownEnded("party_advertise")) {
            leaderProfile.addCooldown("party_advertise", 300_000);

            setOpen(true);
            for (Profile profile : ProfileService.get().profiles.values()) {
                MessagesLocale.PARTY_ADVERTISE_MESSAGE.send(profile.getPlayerUUID(), TagResolver.resolver(
                        Placeholder.parsed("leader", getLeaderName()),
                        TagResolver.resolver("join", Tag.styling(ClickEvent.runCommand("/party joinad " + getLeaderName())))
                ));
            }

            return true;
        }

        return false;
    }

}
