package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.impl.Profile;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

@UtilityClass
public class ServerUtils {

    public void info(String message) {
        Neptune.get().getLogger().info(message);
    }

    public void error(String message) {
        Neptune.get().getLogger().severe(message);
    }

    public void broadcast(MessagesLocale message) {
        for (Profile profile : ProfileService.get().profiles.values()) {
            if (profile.getPlayer() != null && profile.getPlayer().isOnline()) {
                message.send(profile.getPlayerUUID(), TagResolver.empty());
            }
        }
    }

    public void broadcast(MessagesLocale message, TagResolver resolver) {
        for (Profile profile : ProfileService.get().profiles.values()) {
            if (profile.getPlayer() != null && profile.getPlayer().isOnline()) {
                message.send(profile.getPlayerUUID(), resolver);
            }
        }
    }
}