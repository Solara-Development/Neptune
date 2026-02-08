package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class KitPlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("kit");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (match != null && !match.isEnded()) return match.getKit().getDisplayName();
        else if (profile.getState().equals(ProfileState.IN_KIT_EDITOR))
            return profile.getGameData().getKitEditor().getDisplayName();
        else if (profile.getState().equals(ProfileState.IN_QUEUE))
            return QueueService.get().get(player.getUniqueId()).getKit().getDisplayName();
        else return "";
    }
}
