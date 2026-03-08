package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class PartyPlaceholders implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.startsWith("party_");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        String p = string.split("_")[1];
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return null;
        Party party = profile.getGameData().getParty();
        if (party == null) return string;
        switch (p) {
            case "leader" -> { return party.getLeaderName(); }
            case "size" -> { return String.valueOf(party.getUsers().size()); }
            case "max" -> { return String.valueOf(party.getMaxUsers()); }
        }
        return null;
    }
}
