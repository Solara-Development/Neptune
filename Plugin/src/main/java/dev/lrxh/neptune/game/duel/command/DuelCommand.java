package dev.lrxh.neptune.game.duel.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.duel.DuelRequest;
import dev.lrxh.neptune.game.duel.menu.KitSelectMenu;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelCommand {

    @Command(name = "", desc = "", usage = "<player>")
    public void duel(@Sender Player player, Player target) {
        Profile userProfile = API.getProfile(player);
        Profile targetProfile = API.getProfile(target);

        if (userProfile.getMatch() != null) {
            MessagesLocale.YOU_CANT_SEND_DUEL.send(player.getUniqueId());
            return;
        }

        if (player.getName().equalsIgnoreCase(target.getName())) {
            MessagesLocale.CANT_DUEL_SELF.send(player.getUniqueId());
            return;
        }

        if (targetProfile.getMatch() != null || targetProfile.hasState(ProfileState.IN_KIT_EDITOR)
                || !targetProfile.getSettingData().isAllowDuels()) {
            MessagesLocale.PLAYER_CANT_ACCEPT_DUEL.send(player.getUniqueId());
            return;
        }

        if (targetProfile.getGameData().getRequests().contains(player.getUniqueId())) {
            MessagesLocale.DUEL_ALREADY_SENT.send(player.getUniqueId(),
                    new Replacement("<player>", target.getName()));
            return;
        }

        if (userProfile.getState().equals(ProfileState.IN_PARTY)
                && !targetProfile.getState().equals(ProfileState.IN_PARTY)
                || targetProfile.getState().equals(ProfileState.IN_PARTY)
                && !userProfile.getState().equals(ProfileState.IN_PARTY)) {
            MessagesLocale.YOU_CANT_SEND_DUEL.send(player.getUniqueId());
            return;
        }

        if (targetProfile.getState().equals(ProfileState.IN_PARTY)
                && !targetProfile.getGameData().getParty().isLeader(target.getUniqueId())) {
            MessagesLocale.PLAYER_CANT_ACCEPT_DUEL.send(player.getUniqueId());
            return;
        }

        new KitSelectMenu(target.getUniqueId(), userProfile.getState().equals(ProfileState.IN_PARTY)).open(player);
    }

    @Command(name = "accept-uuid", desc = "", usage = "<uuid>", hidden = true)
    public void acceptUUID(@Sender Player player, UUID uuid) {
        Profile profile = API.getProfile(player);
        GameData playerGameData = profile.getGameData();

        if (profile.getMatch() != null || profile.getState().equals(ProfileState.IN_SPECTATOR)
                || profile.hasState(ProfileState.IN_KIT_EDITOR) || profile.hasState(ProfileState.IN_QUEUE)) {
            MessagesLocale.YOU_CANT_SEND_DUEL.send(player.getUniqueId());
            return;
        }

        Player target = Bukkit.getPlayer(uuid);
        if (target == null) {
            MessagesLocale.DUEL_NOT_ONLINE.send(player.getUniqueId());
            return;
        }

        Profile targetProfile = API.getProfile(target);
        DuelRequest duelRequest = (DuelRequest) playerGameData.getRequests().get(uuid);

        if (duelRequest == null) {
            MessagesLocale.YOU_DONT_HAVE_DUEL_REQUEST.send(player.getUniqueId());
            return;
        }

        if (!duelRequest.isParty() && targetProfile.getState().equals(ProfileState.IN_PARTY)
                || duelRequest.isParty() && !profile.getState().equals(ProfileState.IN_PARTY)
                || duelRequest.isParty() && !targetProfile.getState().equals(ProfileState.IN_PARTY)) {
            MessagesLocale.DUEL_REQUEST_COULDNT_BE_ACCEPTED.send(player.getUniqueId());
            return;
        }

        profile.acceptDuel(uuid);
    }

    @Command(name = "specific", desc = "", usage = "<player> <kit> <rounds>")
    public void duel(@Sender Player player, Player target, Kit kit, int rounds) {
        Profile userProfile = API.getProfile(player);
        Profile targetProfile = API.getProfile(target);

        if (userProfile.getMatch() != null) {
            MessagesLocale.YOU_CANT_SEND_DUEL.send(player.getUniqueId());
            return;
        }

        if (player.getName().equalsIgnoreCase(target.getName())) {
            MessagesLocale.CANT_DUEL_SELF.send(player.getUniqueId());
            return;
        }

        if (targetProfile.getMatch() != null || targetProfile.hasState(ProfileState.IN_KIT_EDITOR)
                || !targetProfile.getSettingData().isAllowDuels()) {
            MessagesLocale.PLAYER_CANT_ACCEPT_DUEL.send(player.getUniqueId());
            return;
        }

        if (targetProfile.getGameData().getRequests().contains(player.getUniqueId())) {
            MessagesLocale.DUEL_ALREADY_SENT.send(player.getUniqueId(),
                    new Replacement("<player>", target.getName()));
            return;
        }

        if (userProfile.getState().equals(ProfileState.IN_PARTY)
                && !targetProfile.getState().equals(ProfileState.IN_PARTY)
                || targetProfile.getState().equals(ProfileState.IN_PARTY)
                && !userProfile.getState().equals(ProfileState.IN_PARTY)) {
            MessagesLocale.YOU_CANT_SEND_DUEL.send(player.getUniqueId());
            return;
        }

        if (targetProfile.getState().equals(ProfileState.IN_PARTY)
                && !targetProfile.getGameData().getParty().isLeader(target.getUniqueId())) {
            MessagesLocale.PLAYER_CANT_ACCEPT_DUEL.send(player.getUniqueId());
            return;
        }

        boolean party = userProfile.getState().equals(ProfileState.IN_PARTY)
                && targetProfile.getState().equals(ProfileState.IN_PARTY);

        kit.getRandomArena().thenAccept(arena -> {
            DuelRequest duelRequest = new DuelRequest(player.getUniqueId(), kit, arena, party, rounds);
            targetProfile.sendDuel(duelRequest);
        });
    }

    @Command(name = "deny-uuid", desc = "", usage = "<uuid>", hidden = true)
    public void denyUUID(@Sender Player player, UUID uuid) {
        Profile profile = API.getProfile(player);
        GameData playerGameData = profile.getGameData();

        DuelRequest duelRequest = (DuelRequest) playerGameData.getRequests().get(uuid);
        if (duelRequest == null) {
            MessagesLocale.YOU_DONT_HAVE_DUEL_REQUEST.send(player.getUniqueId());
            return;
        }

        Player sender = Bukkit.getPlayer(uuid);
        if (sender == null) return;

        MessagesLocale.DUEL_DENY_SENDER.send(player.getUniqueId(), new Replacement("<player>", sender.getName()));
        MessagesLocale.DUEL_DENY_RECEIVER.send(uuid, new Replacement("<player>", player.getName()));

        duelRequest.getArena().remove();
        playerGameData.removeRequest(uuid);
    }
}
