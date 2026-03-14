package dev.lrxh.neptune;

import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class API {

    public static Profile getProfile(UUID playerUUID) {
        return ProfileService.get().getByUUID(playerUUID);
    }

    public static Profile getProfile(Player player) {
        return ProfileService.get().getByUUID(player.getUniqueId());
    }

    public static CompletableFuture<Profile> getProfile(OfflinePlayer player) {
        return ProfileService.get()._getProfile(player.getUniqueId());
    }
}
