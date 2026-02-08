package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class SizePlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("size");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return string;
        Party party = profile.getGameData().getParty();
        if (party == null) return "";
        return String.valueOf(party.getUsers().size());
    }
}
