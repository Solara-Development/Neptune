package dev.lrxh.neptune.feature.customkit.queue;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CustomKitQueueService {
    private static CustomKitQueueService instance;

    /** host UUID -> the kit they are hosting (one open listing per host). */
    private final Map<UUID, CustomKit> listings = new ConcurrentHashMap<>();

    public static CustomKitQueueService get() {
        if (instance == null) instance = new CustomKitQueueService();
        return instance;
    }

    public Map<UUID, CustomKit> getListings() {
        return listings;
    }

    public void host(Player player, CustomKit kit) {
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        if (!profile.hasState(ProfileState.IN_LOBBY) || profile.getGameData().getParty() != null) {
            MessagesLocale.CUSTOM_KIT_CANT_HOST.send(player.getUniqueId());
            return;
        }
        if (kit.getArenaNames().isEmpty()) {
            MessagesLocale.CUSTOM_KIT_HOST_NEED_ARENA.send(player.getUniqueId());
            return;
        }
        listings.put(player.getUniqueId(), kit);
        player.closeInventory();
        profile.setState(ProfileState.IN_QUEUE);
    }

    public void unhost(UUID uuid) {
        listings.remove(uuid);
    }

    public void join(Player joiner, UUID hostUUID) {
        CustomKit kit = listings.get(hostUUID);
        if (kit == null) {
            MessagesLocale.CUSTOM_KIT_UNAVAILABLE.send(joiner.getUniqueId());
            return;
        }
        if (joiner.getUniqueId().equals(hostUUID)) {
            MessagesLocale.CUSTOM_KIT_JOIN_OWN.send(joiner.getUniqueId());
            return;
        }
        Profile joinerProfile = API.getProfile(joiner);
        if (joinerProfile == null || !joinerProfile.hasState(ProfileState.IN_LOBBY)
                || joinerProfile.getGameData().getParty() != null) {
            MessagesLocale.CUSTOM_KIT_JOIN_CANT.send(joiner.getUniqueId());
            return;
        }
        Player host = Bukkit.getPlayer(hostUUID);
        Profile hostProfile = host == null ? null : API.getProfile(host);
        if (host == null || hostProfile == null || !hostProfile.hasState(ProfileState.IN_QUEUE)) {
            MessagesLocale.CUSTOM_KIT_HOST_UNAVAILABLE.send(joiner.getUniqueId());
            unhost(hostUUID);
            return;
        }
        listings.remove(hostUUID);
        joiner.closeInventory();
        start(joiner, host, kit);
    }

    /** Builds a throwaway kit and starts an unranked (duel) match so no stats are touched. */
    private void start(Player joiner, Player host, CustomKit kit) {
        Kit transientKit = kit.toTransientKit();
        transientKit.getRandomArena().thenAccept(arena -> Bukkit.getScheduler().runTask(Neptune.get(), () -> {
            if (arena == null) {
                listings.put(host.getUniqueId(), kit);
                MessagesLocale.CUSTOM_KIT_NO_ARENA.send(joiner.getUniqueId());
                MessagesLocale.CUSTOM_KIT_HOST_NO_ARENA.send(host.getUniqueId());
                return;
            }
            arena.getWhitelistedBlocks().clear();
            arena.getWhitelistedBlocks().addAll(kit.getWhitelistedBlocks());
            MatchService.get().startMatch(new Participant(joiner), new Participant(host), transientKit, arena, true, 1);
        }));
    }
}
