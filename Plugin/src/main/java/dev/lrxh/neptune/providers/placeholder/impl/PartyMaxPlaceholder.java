package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class PartyMaxPlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("party-max");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Party party = API.getProfile(player.getUniqueId()).getGameData().getParty();
        if (party == null) return "";
        return String.valueOf(party.getMaxUsers());
    }
}
