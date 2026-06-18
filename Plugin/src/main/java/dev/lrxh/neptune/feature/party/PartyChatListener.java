package dev.lrxh.neptune.feature.party;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.profile.impl.Profile;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PartyChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);

        if (!profile.getGameData().isPartyChatEnabled()) {
            return;
        }

        Party party = profile.getGameData().getParty();
        if (party == null) {
            profile.getGameData().setPartyChatEnabled(false);
            return;
        }

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        event.setCancelled(true);
        party.chat(player.getUniqueId(), player.getName(), message);
    }
}
