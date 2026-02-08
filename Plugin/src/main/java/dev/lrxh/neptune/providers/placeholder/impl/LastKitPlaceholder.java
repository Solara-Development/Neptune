package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class LastKitPlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("lastKit");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return string;
        return profile.getGameData().getLastPlayedKit().isEmpty() ? "N/A" : profile.getGameData().getLastPlayedKit();
    }
}
