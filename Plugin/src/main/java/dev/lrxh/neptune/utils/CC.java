package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;

import java.util.Arrays;
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

    public Component returnMessage(Player player, String message, Replacement... replacements) {
        String miniMessageInput = convertLegacyToMiniMessage(message);

        TagResolver resolver = TagResolver.resolver(
                Arrays.stream(replacements)
                        .map(replacement -> {
                            @Subst("")
                            String key = replacement.getPlaceholder().replaceAll("^<|>$", "").toLowerCase();
                            return TagResolver.resolver(
                                    key,
                                    Placeholder.component(key, replacement.getReplacement()).tag());
                        })
                        .toArray(TagResolver[]::new));
        return PlaceholderUtil.format(MiniMessage.miniMessage().deserialize(miniMessageInput, resolver), player);
    }
}