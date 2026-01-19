package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KitDeathsPlaceholder implements Placeholder {
    private final Pattern PATTERN = Pattern.compile("(.*)_deaths");

    @Override
    public boolean match(String string) {
        return PATTERN.matcher(string).matches();
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return string;

        Matcher matcher = PATTERN.matcher(string);
        if (!matcher.matches()) return string;
        KitData data = profile.getGameData().get(KitService.get().getKitByName(matcher.group(1)));
        if (data == null) return string;

        return String.valueOf(data.getDeaths());
    }
}
