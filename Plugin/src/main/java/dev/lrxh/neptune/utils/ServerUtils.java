package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.entity.Player;

@UtilityClass
public class ServerUtils {

    public void info(String message) {
        Neptune.get().getLogger().info(message);
    }

    public void error(String message) {
        Neptune.get().getLogger().severe(message);
    }

    public void broadcast(MessagesLocale message) {
        for (Player player : Neptune.get().getServer().getOnlinePlayers()) {
            message.send(player.getUniqueId(), TagResolver.empty());
        }
    }
    public void broadcast(MessagesLocale message, TagResolver resolver) {
        for (Player player : Neptune.get().getServer().getOnlinePlayers()) {
            message.send(player.getUniqueId(), resolver);
        }
    }
}