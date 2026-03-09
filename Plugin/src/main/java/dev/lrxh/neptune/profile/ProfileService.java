package dev.lrxh.neptune.profile;

import dev.lrxh.api.profile.IProfile;
import dev.lrxh.api.profile.IProfileService;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;

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
                .thenAccept(profile -> profiles.put(player.getUniqueId(), profile))
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Neptune.get().getLogger().severe("Error occurred while loading profile for player: " + player.getName());
                        player.kick(CC.color("<aqua>[Neptune] <dark_red>An error occurred while loading your profile information! Report to the developers."));
                    }
                });
    }

    public CompletableFuture<Profile> createFakeProfile(UUID uuid) {
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
        profiles.values().forEach(Profile::save);
    }

    public Profile getByUUID(UUID playerUUID) {
        return profiles.get(playerUUID);
    }

    @Override
    public CompletableFuture<IProfile> getProfile(UUID uuid) {
        return _getProfile(uuid).thenApply(p -> p);
    }

    public CompletableFuture<Profile> _getProfile(UUID uuid) {
        Profile profile = getByUUID(uuid);
        return (profile != null)
                ? CompletableFuture.completedFuture(profile)
                : createFakeProfile(uuid).thenApply(p -> p);
    }
}
