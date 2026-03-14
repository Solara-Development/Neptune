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
    private final MiniMessage mm = MiniMessage.miniMessage();
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
        String converted = replaceLegacy(message);
        Component parsed = mm.deserialize(converted);

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

    public String replaceLegacy(String text) {
        text = text
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
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
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<underlined>")
                .replace("&o", "<italic>")
                .replace("&r", "<reset>");
        Pattern LEGACY_HEX = Pattern.compile(
                "[§&]x[§&]([0-9a-f])[§&]([0-9a-f])[§&]([0-9a-f])[§&]([0-9a-f])[§&]([0-9a-f])[§&]([0-9a-f])", Pattern.CASE_INSENSITIVE);
        text = LEGACY_HEX.matcher(text)
            .replaceAll("<#$1$2$3$4$5$6>");
        return text.replaceAll("(?i)&#([a-f0-9]{6})", "<#$1>");
    }
    public static Component replaceLegacy(Component input) {
        return mm.deserialize(replaceLegacy(mm.serialize(input)));
    }
    public Component returnMessage(Player player, String message) {
        return returnMessage(player, message, TagResolver.empty());
    }
    public Component returnMessage(Player player, String message, TagResolver resolver) {
        String minimessageInput = replaceLegacy(message);
        Component component = mm.deserialize(minimessageInput, TagResolver.resolver(PlaceholderUtil.getPlaceholders(player), resolver));
        if (Neptune.get().isPlaceholder()) {
            try {
                return replaceLegacy(PAPIComponents.setPlaceholders(player, component));
            } catch (NoClassDefFoundError e) {
                ServerUtils.error("Please update your PlaceholderAPI version to at least 2.12.1: https://modrinth.com/plugin/placeholderapi");
            }
        }
        return replaceLegacy(component);
    }
    public Component returnMessage(Player player, Component message) {
        return returnMessage(player, message, TagResolver.empty());
    }
    public Component returnMessage(Player player, Component message, TagResolver resolver) {
        String serialized = mm.serialize(message);
        return returnMessage(player, serialized, resolver);
    }
    public Component returnMessage(Component message) {
        return returnMessage(message, TagResolver.empty());
    }
    public Component returnMessage(Component message, TagResolver resolver) {
        return returnMessage(mm.serialize(message), resolver);
    }
    public Component returnMessage(String message) {
        return returnMessage(message, TagResolver.empty());
    }
    public Component returnMessage(String message, TagResolver resolver) {
        return MiniMessage.miniMessage().deserialize(replaceLegacy(message), resolver);
    }
    public List<Component> getComponentsArray(Player player, List<String> lines)  {
        List<Component> components = new ArrayList<>();
        for (String string : lines) {
            components.add(CC.returnMessage(player, string));
        }
        return components;
    }
}