package dev.lrxh.neptune.providers.placeholder;

import org.bukkit.OfflinePlayer;

public interface PAPIPlaceholder {
    boolean match(String string);

    String parse(OfflinePlayer player, String string);
}
