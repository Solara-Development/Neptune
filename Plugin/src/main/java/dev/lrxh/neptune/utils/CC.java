package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PAPIComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class CC {
    public TextComponent error(String message) {
        return color(MessagesLocale.ERROR_MESSAGE.getString().replace("<error>", message));
    }

    public TextComponent success(String text) {
        return color("&a[+] " + text);
    }

    public TextComponent info(String text) {
        return color("&7[~] " + text);
    }

    public TextComponent color(String message) {
        String converted = convertLegacyToMiniMessage(message);
        Component parsed = MiniMessage.miniMessage().deserialize(converted);

        boolean hasItalic = message.contains("&o") || converted.contains("<italic>");
        Component fixed = parsed.decorationIfAbsent(TextDecoration.ITALIC,
                hasItalic ? TextDecoration.State.TRUE : TextDecoration.State.FALSE);

        if (fixed instanceof TextComponent textComponent) {
            return textComponent;
        }

        return Component.text()
                .append(fixed)
                .decorationIfAbsent(TextDecoration.ITALIC,
                        hasItalic ? TextDecoration.State.TRUE : TextDecoration.State.FALSE)
                .build();
    }

    private String convertLegacyToMiniMessage(String text) {
        text = text
                .replace("&c", "<red>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&d", "<light_purple>")
                .replace("&l", "<bold>")
                .replace("&o", "<italic>")
                .replace("&n", "<underlined>")
                .replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>")
                .replace("&r", "<reset>");
        Pattern LEGACY_HEX = Pattern.compile(
                "[§&]x[§&]([0-9a-f])[§&]([0-9a-f])[§&]([0-9a-f])[§&]([0-9a-f])[§&]([0-9a-f])[§&]([0-9a-f])", Pattern.CASE_INSENSITIVE);
        text = LEGACY_HEX.matcher(text)
            .replaceAll("<#$1$2$3$4$5$6>");
        return text.replaceAll("(?i)&#([a-f0-9]{6})", "<#$1>");
    }
    public Component returnMessage(Player player, String message) {
        return returnMessage(player, message, TagResolver.empty());
    }
    public Component returnMessage(Player player, String message, TagResolver resolver) {
        String miniMessageInput = convertLegacyToMiniMessage(message);
        Component component = MiniMessage.miniMessage().deserialize(miniMessageInput, TagResolver.resolver(resolver, PlaceholderUtil.getPlaceholders(player)));
        if (Neptune.get().isPlaceholder()) {
            try {
                return PAPIComponents.setPlaceholders(player, component);
            } catch (NoClassDefFoundError e) {
                ServerUtils.error("Please update your PlaceholderAPI version to at least 2.12.1: https://modrinth.com/plugin/placeholderapi");
            }
        }
        return component;
    }
    public Component returnMessage(Player player, Component message) {
        return returnMessage(player, message, TagResolver.empty());
    }
    public Component returnMessage(Player player, Component message, TagResolver resolver) {
        String serialized = convertLegacyToMiniMessage(MiniMessage.miniMessage().serialize(message));
        Component component = MiniMessage.miniMessage().deserialize(serialized, TagResolver.resolver(resolver, PlaceholderUtil.getPlaceholders(player)));
        if (Neptune.get().isPlaceholder()) {
            try {
                return PAPIComponents.setPlaceholders(player, component);
            } catch (NoClassDefFoundError e) {
                ServerUtils.error("Please update your PlaceholderAPI version to at least 2.12.1: https://modrinth.com/plugin/placeholderapi");
            }
        }
        return component;
    }

    public List<Component> getComponentsArray(Player player, List<String> lines)  {
        List<Component> components = new ArrayList<>();
        for (String string : lines) {
            components.add(CC.returnMessage(player, string));
        }
        return components;
    }
}