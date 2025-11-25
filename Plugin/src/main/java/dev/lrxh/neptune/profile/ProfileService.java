package dev.lrxh.neptune.profile;

import dev.lrxh.api.profile.IProfile;
import dev.lrxh.api.profile.IProfileService;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileService implements IProfileService {
    private static ProfileService instance;
    public final ConcurrentHashMap<UUID, Profile> profiles = new ConcurrentHashMap<>();
    private final Neptune plugin;

    public ProfileService() {
        this.plugin = Neptune.get();
    }

    public static ProfileService get() {
        if (instance == null)
            instance = new ProfileService();

        return instance;
    }

    public CompletableFuture<Void> createProfile(Player player) {
        return Profile.create(player.getName(), player.getUniqueId(), plugin, false)
                .thenAccept(profile -> profiles.put(player.getUniqueId(), profile));
    }

    public CompletableFuture<Profile> createProfile(UUID uuid) {
        return Profile.create("username", uuid, plugin, true).thenApply(profile -> profile);
    }

    public void removeProfile(UUID playerUUID) {
        QueueService.get().remove(playerUUID);
        Profile profile = profiles.get(playerUUID);
        profile.disband();

        Profile.save(profile);

        profiles.remove(playerUUID);
    }

    public void saveAll() {
        // Create defensive copy to prevent ConcurrentModificationException
        // if players join/leave during save operation
        for (Profile profile : new ArrayList<>(profiles.values())) {
            Profile.save(profile);
        }
    }

    public Profile getByUUID(UUID playerUUID) {
        // Direct lookup first
        Profile profile = profiles.get(playerUUID);
        if (profile != null)
            return profile;

        // Fallback: iterate if direct lookup fails (shouldn't happen with
        // ConcurrentHashMap)
        for (Profile p : profiles.values()) {
            if (p.getPlayerUUID().equals(playerUUID)) {
                return p;
            }
        }

        return null;
    }

    @Override
    public CompletableFuture<IProfile> getProfile(UUID uuid) {
        return _getProfile(uuid).thenApply(p -> p);
    }

    public CompletableFuture<Profile> _getProfile(UUID uuid) {
        Profile profile = getByUUID(uuid);
        return (profile != null)
                ? CompletableFuture.completedFuture(profile)
                : createProfile(uuid).thenApply(p -> p);
    }
}
