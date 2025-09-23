package dev.lrxh.neptune.profile;

import dev.lrxh.api.profile.IProfile;
import dev.lrxh.api.profile.IProfileService;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.entity.Player;

import java.util.IdentityHashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProfileService implements IProfileService {
    private static ProfileService instance;
    public final IdentityHashMap<UUID, Profile> profiles = new IdentityHashMap<>();
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
        return Profile.create(player.getName(), player.getUniqueId(), plugin, false).thenAccept(profile -> {
            profiles.put(player.getUniqueId(), profile);
        });
    }

    public CompletableFuture<Profile> createProfile(UUID uuid) {
        return Profile.create("username", uuid, plugin, true).thenApply(profile -> {
            return profile;
        });
    }

    public void removeProfile(UUID playerUUID) {
        QueueService.get().remove(playerUUID);
        Profile profile = profiles.get(playerUUID);
        profile.disband();

        Profile.save(profile);

        profiles.remove(playerUUID);
    }

    public void saveAll() {
        for (Profile profile : profiles.values()) {
            Profile.save(profile);
        }
    }

    public Profile getByUUID(UUID playerUUID) {
        Profile profile = profiles.get(playerUUID);
        if (profile != null)
            return profile;

        for (UUID uuid : profiles.keySet()) {
            if (uuid.toString().equals(playerUUID.toString()))
                return profiles.get(uuid);
        }

        return null;
    }

    @Override
    public CompletableFuture<IProfile> getProfile(UUID uuid) {
        Profile profile = getByUUID(uuid);
        return (profile != null)
                ? CompletableFuture.completedFuture(profile)
                : createProfile(uuid).thenApply(p -> (IProfile) p);
    }


}
