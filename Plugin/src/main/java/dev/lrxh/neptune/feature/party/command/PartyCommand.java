package dev.lrxh.neptune.feature.party.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.impl.PartyRequest;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyCommand {

    @Command(name = "help", desc = "")
    public void help(@Sender Player player) {
        MessagesLocale.PARTY_HELP.send(player.getUniqueId());
    }

    @Command(name = "create", desc = "")
    public void create(@Sender Player player) {
        Profile profile = API.getProfile(player);
        if (!profile.hasState(ProfileState.IN_LOBBY)) {
            MessagesLocale.PARTY_CANNOT_CREATE.send(player.getUniqueId());
            return;
        }
        profile.createParty();
    }

    @Command(name = "join", desc = "", usage = "<player>")
    public void join(@Sender Player player, Player target) {
        Party party = API.getProfile(target).getGameData().getParty();

        if (party == null) {
            MessagesLocale.PARTY_NOT_IN_PARTY.send(player.getUniqueId(), new Replacement("<player>", target.getName()));
            return;
        }
        if (!party.getLeader().equals(target.getUniqueId())) {
            MessagesLocale.PARTY_NOT_LEADER.send(player.getUniqueId(), new Replacement("<player>", target.getName()));
            return;
        }
        if (!party.isOpen()) {
            MessagesLocale.PARTY_PRIVATE.send(player.getUniqueId(), new Replacement("<player>", target.getName()));
            return;
        }

        Profile profile = API.getProfile(player);

        if (profile.getGameData().getParty() != null) {
            MessagesLocale.PARTY_ALREADY_IN.send(player.getUniqueId());
            return;
        }

        if (!profile.hasState(ProfileState.IN_LOBBY)) {
            MessagesLocale.PARTY_CANNOT_CREATE.send(player.getUniqueId());
            return;
        }

        party.accept(player.getUniqueId());
    }

    @Command(name = "disband", desc = "")
    public void disband(@Sender Player player) {
        API.getProfile(player).disband();
    }

    @Command(name = "leave", desc = "")
    public void leave(@Sender Player player) {
        API.getProfile(player).disband();
    }

    @Command(name = "invite", desc = "", usage = "<player>")
    public void invite(@Sender Player player, Player target) {
        Profile targetProfile = API.getProfile(target);
        Profile profile = API.getProfile(player);
        Party party = profile.getGameData().getParty();

        if (player == target) {
            MessagesLocale.PARTY_INVITE_OWN.send(player.getUniqueId());
            return;
        }

        if (party == null) {
            Party createdParty = profile.createParty();
            party = (createdParty == null) ? profile.getGameData().getParty() : createdParty;
        }

        if (!party.getLeader().equals(player.getUniqueId())) {
            MessagesLocale.PARTY_NO_PERMISSION.send(player.getUniqueId());
            return;
        }

        if (!targetProfile.getSettingData().isAllowParty()) {
            MessagesLocale.PARTY_DISABLED.send(player.getUniqueId());
            return;
        }

        if (targetProfile.getGameData().getParty() != null) {
            MessagesLocale.PARTY_ALREADY_PARTY.send(player.getUniqueId(),
                    new Replacement("<player>", target.getName()));
            return;
        }

        if (profile.getGameData().getRequests().contains(player.getUniqueId()) ||
                targetProfile.getGameData().getRequests().contains(player.getUniqueId())) {
            MessagesLocale.PARTY_ALREADY_SENT.send(player.getUniqueId(),
                    new Replacement("<player>", target.getName()));
            return;
        }

        if (party.getUsers().size() > party.getMaxUsers()) {
            MessagesLocale.PARTY_MAX_SIZE.send(player.getUniqueId());
            return;
        }

        party.invite(target.getUniqueId());
        MessagesLocale.PARTY_INVITED.send(player.getUniqueId(),
                new Replacement("<player>", target.getName()));
    }

    @Command(name = "accept", desc = "", usage = "<uuid>")
    public void accept(@Sender Player player, UUID uuid) {
        Profile profile = API.getProfile(player);
        if (!profile.getState().equals(ProfileState.IN_LOBBY))
            return;

        if (profile.getGameData().getParty() != null) {
            MessagesLocale.PARTY_ALREADY_IN.send(player.getUniqueId());
            return;
        }

        PartyRequest request = (PartyRequest) profile.getGameData().getRequests().get(uuid);
        if (request != null) {
            request.getParty().accept(player.getUniqueId());
        }
    }

    @Command(name = "kick", desc = "", usage = "<player>")
    public void kick(@Sender Player player, Player target) {
        Party party = API.getProfile(target).getGameData().getParty();
        if (party == null || !party.getLeader().equals(player.getUniqueId())) {
            MessagesLocale.PARTY_NOT_IN_SAME_PARTY.send(player.getUniqueId(),
                    new Replacement("<player>", player.getName()));
            return;
        }

        party.kick(target.getUniqueId());
    }

    @Command(name = "transfer", desc = "", usage = "<player>")
    public void transfer(@Sender Player player, Player target) {
        if (player == target) {
            MessagesLocale.PARTY_TRANSFER_OWN.send(player.getUniqueId());
            return;
        }

        Party party = API.getProfile(player).getGameData().getParty();
        Party targetParty = API.getProfile(target).getGameData().getParty();

        if (party == null) {
            MessagesLocale.PARTY_NOT_IN.send(player.getUniqueId());
            return;
        }
        if (!party.equals(targetParty)) {
            MessagesLocale.PARTY_NOT_IN_SAME_PARTY.send(player.getUniqueId(),
                    new Replacement("<player>", target.getName()));
            return;
        }
        if (!party.getLeader().equals(player.getUniqueId())) {
            MessagesLocale.PARTY_NO_PERMISSION.send(player.getUniqueId());
            return;
        }

        party.transfer(player, target);
    }

    @Command(name = "advertise", desc = "")
    @Require("neptune.party.advertise")
    public void advertise(@Sender Player player) {
        Party party = API.getProfile(player).getGameData().getParty();
        if (party == null) {
            MessagesLocale.PARTY_NOT_IN.send(player.getUniqueId());
            return;
        }
        if (!party.getLeader().equals(player.getUniqueId())) {
            MessagesLocale.PARTY_NO_PERMISSION.send(player.getUniqueId());
            return;
        }

        party.advertise();
    }
}
