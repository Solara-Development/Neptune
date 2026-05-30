package dev.lrxh.neptune.feature.customkit.listener;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.feature.customkit.CustomKitService;
import dev.lrxh.neptune.feature.customkit.menu.CustomKitManageMenu;
import dev.lrxh.neptune.feature.customkit.queue.CustomKitQueueService;
import dev.lrxh.neptune.profile.impl.Profile;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class CustomKitListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        CustomKitService service = CustomKitService.get();
        CustomKitService.Input input = service.inputType(player.getUniqueId());
        if (input == null) return;

        event.setCancelled(true);
        String message = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();
        CustomKit kit = service.inputKit(player.getUniqueId());
        service.clearInput(player.getUniqueId());

        if (message.equalsIgnoreCase("cancel")) {
            MessagesLocale.CUSTOM_KIT_CANCELLED.send(player.getUniqueId());
            return;
        }

        switch (input) {
            case CREATE -> {
                CustomKit created = service.create(player.getUniqueId(), message);
                if (created == null) {
                    MessagesLocale.CUSTOM_KIT_CREATE_FAIL.send(player.getUniqueId());
                    return;
                }
                Profile profile = API.getProfile(player);
                if (profile != null) Profile.save(profile);
                MessagesLocale.CUSTOM_KIT_CREATED.send(player.getUniqueId(),
                        Placeholder.parsed("kit", created.getDisplayName()));
                new CustomKitManageMenu(created).open(player);
            }
            case HEALTH -> {
                if (kit == null) return;
                try {
                    double health = Double.parseDouble(message);
                    if (health < 1 || health > 40) {
                        MessagesLocale.CUSTOM_KIT_HEALTH_RANGE.send(player.getUniqueId());
                        return;
                    }
                    kit.setHealth(health);
                    Profile profile = API.getProfile(player);
                    if (profile != null) Profile.save(profile);
                    MessagesLocale.CUSTOM_KIT_HEALTH_SET.send(player.getUniqueId(),
                            Placeholder.unparsed("health", String.valueOf(health)));
                    new CustomKitManageMenu(kit).open(player);
                } catch (NumberFormatException e) {
                    MessagesLocale.CUSTOM_KIT_HEALTH_INVALID.send(player.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CustomKitService.get().clearInput(event.getPlayer().getUniqueId());
        CustomKitQueueService.get().unhost(event.getPlayer().getUniqueId());
    }
}
