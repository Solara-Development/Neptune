package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.OfflinePlayer;

public class PingPlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("ping");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        return String.valueOf(PlayerUtil.getPing(player.getUniqueId()));
    }
}
