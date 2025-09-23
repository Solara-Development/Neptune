package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Visibility {
    private final UUID uuid;

    public Visibility(UUID playerUUID) {
        this.uuid = playerUUID;
        handle(playerUUID);
    }

    public void handle() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            handle(players.getUniqueId());
        }
    }

    public void handle(UUID otherUUID) {
        Player viewerPlayer = Bukkit.getPlayer(uuid);
        Player otherPlayer = Bukkit.getPlayer(otherUUID);
        if (viewerPlayer == null || otherPlayer == null || viewerPlayer.equals(otherPlayer)) {
            return;
        }

        Profile viewerProfile = API.getProfile(uuid);
        Profile otherProfile = API.getProfile(otherUUID);

        if (viewerProfile.hasState(ProfileState.IN_LOBBY)) {
            if (!viewerProfile.getSettingData().isPlayerVisibility()) {
                viewerPlayer.hidePlayer(Neptune.get(), otherPlayer);
                return;
            } else {
                viewerPlayer.showPlayer(Neptune.get(), otherPlayer);
            }
        }

        if (viewerProfile.hasState(ProfileState.IN_LOBBY)) {
            if (!otherProfile.getSettingData().isPlayerVisibility()) {
                otherPlayer.hidePlayer(Neptune.get(), viewerPlayer);
                return;
            } else {
                otherPlayer.showPlayer(Neptune.get(), viewerPlayer);
            }
        }

        if (viewerProfile.hasState(ProfileState.IN_KIT_EDITOR)) {
            viewerPlayer.hidePlayer(Neptune.get(), otherPlayer);
            otherPlayer.hidePlayer(Neptune.get(), viewerPlayer);
            return;
        }

        if (otherProfile.hasState(ProfileState.IN_KIT_EDITOR)) {
            otherPlayer.hidePlayer(Neptune.get(), viewerPlayer);
            viewerPlayer.hidePlayer(Neptune.get(), otherPlayer);
        }
    }

    public boolean has(Profile viewerProfile, Profile otherProfile, ProfileState... states) {
        return viewerProfile.hasState(states) && otherProfile.hasState(states);
    }
}
