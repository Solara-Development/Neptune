package dev.lrxh.neptune.feature.event;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.event.task.EventBeginTask;
import dev.lrxh.neptune.feature.hotbar.HotbarService;import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Getter
public class EventService extends IService {
    private static EventService instance;
    private AutomatedEvent activeEvent;
    private EventBeginTask beginTask;
    private final Map<UUID, Long> startCooldowns = new HashMap<>();

    public static EventService get() {
        if (instance == null) instance = new EventService();
        return instance;
    }

    public void startEvent(EventType type, Kit kit) {
        startEvent(type, kit, null);
    }

    public void startEvent(EventType type, Kit kit, UUID starterUUID) {
        if (activeEvent != null) {
            if (starterUUID != null) MessagesLocale.EVENT_ALREADY_ACTIVE.send(starterUUID);
            return;
        }
        if (starterUUID != null) {
            int cooldown = SettingsLocale.EVENT_START_COOLDOWN.getInt();
            if (cooldown > 0) {
                Long last = startCooldowns.get(starterUUID);
                if (last != null) {
                    long remaining = cooldown - (System.currentTimeMillis() - last) / 1000;
                    if (remaining > 0) {
                        MessagesLocale.EVENT_COOLDOWN.send(starterUUID,
                                Placeholder.unparsed("time", String.valueOf(remaining)));
                        return;
                    }
                }
                startCooldowns.put(starterUUID, System.currentTimeMillis());
            }
        }
        activeEvent = new AutomatedEvent(type, kit);
        activeEvent.setStarterUUID(starterUUID);

        TagResolver resolver = TagResolver.resolver(
                Placeholder.unparsed("type", type.name()),
                TagResolver.resolver("join", Tag.styling(ClickEvent.runCommand("/event join")))
        );
        for (Profile profile : ProfileService.get().profiles.values()) {
            if (profile.hasState(ProfileState.IN_LOBBY, ProfileState.IN_PARTY)) {
                MessagesLocale.EVENT_ANNOUNCE.send(profile.getPlayerUUID(), resolver);
            }
        }

        // Auto-join the player who started the event
        if (starterUUID != null) {
            joinEvent(starterUUID);
        }

        beginTask = new EventBeginTask(this);
        beginTask.start(0L, 20L);
    }

    public void joinEvent(UUID uuid) {
        if (activeEvent == null || activeEvent.getState() != EventState.WAITING) {
            MessagesLocale.EVENT_NOT_ACTIVE.send(uuid);
            return;
        }
        Profile profile = API.getProfile(uuid);
        if (profile == null || !profile.hasState(ProfileState.IN_LOBBY, ProfileState.IN_PARTY)) {
            MessagesLocale.CANT_DO_THIS_NOW.send(uuid);
            return;
        }
        if (activeEvent.getParticipants().contains(uuid)) {
            MessagesLocale.EVENT_ALREADY_JOINED.send(uuid);
            return;
        }
        if (activeEvent.getParticipants().size() >= 50) {
            MessagesLocale.EVENT_FULL.send(uuid);
            return;
        }
        activeEvent.getParticipants().add(uuid);
        profile.setState(ProfileState.IN_EVENT);
        if (Bukkit.getPlayer(uuid) != null) HotbarService.get().giveItems(Bukkit.getPlayer(uuid));
        MessagesLocale.EVENT_JOIN.send(uuid, Placeholder.unparsed("type", activeEvent.getType().name()));
    }

    public void forceStart() {
        if (beginTask != null) beginTask.forceStart();
    }

    public void endEvent(UUID winnerUUID) {
        if (activeEvent == null) return;
        activeEvent.setState(EventState.ENDED);
        activeEvent.setWinner(winnerUUID);

        String winnerName = winnerUUID != null && Bukkit.getPlayer(winnerUUID) != null
                ? Bukkit.getPlayer(winnerUUID).getName() : "Unknown";

        TagResolver resolver = TagResolver.resolver(
                Placeholder.unparsed("winner", winnerName),
                Placeholder.unparsed("type", activeEvent.getType().name())
        );
        broadcastToAll(MessagesLocale.EVENT_WINNER, resolver);
        resetParticipants();

        Bukkit.getScheduler().runTaskLater(Neptune.get(), () -> activeEvent = null, 60L);
    }

    public void stopEvent() {        if (activeEvent == null) return;
        broadcastToAll(MessagesLocale.EVENT_ENDED, TagResolver.empty());
        resetParticipants();
        activeEvent = null;
    }

    public void cancelEvent() {
        if (activeEvent == null) return;
        broadcastToAll(MessagesLocale.EVENT_CANCELLED, TagResolver.empty());
        resetParticipants();
        activeEvent = null;
    }

    private void resetParticipants() {
        if (activeEvent == null) return;
        for (UUID uuid : new ArrayList<>(activeEvent.getParticipants())) {
            Profile profile = API.getProfile(uuid);
            if (profile == null) continue;
            if (profile.getMatch() != null) continue;
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) PlayerUtil.reset(player);
            profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
        }
    }

    private void broadcastToAll(MessagesLocale message, TagResolver resolver) {
        for (Profile profile : ProfileService.get().profiles.values()) {
            message.send(profile.getPlayerUUID(), resolver);
        }
    }

    public void autoStart() {
        if (activeEvent != null) return;
        List<Kit> eligible = new ArrayList<>();
        for (Kit kit : KitService.get().kits) {
            if (!kit.getArenas().isEmpty()) eligible.add(kit);
        }
        if (eligible.isEmpty()) return;
        Kit kit = eligible.get(new Random().nextInt(eligible.size()));
        EventType[] types = EventType.values();
        EventType type = types[new Random().nextInt(types.length)];
        startEvent(type, kit);
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getMainConfig();
    }

    @Override
    public void load() {}

    @Override
    public void save() {}
}
