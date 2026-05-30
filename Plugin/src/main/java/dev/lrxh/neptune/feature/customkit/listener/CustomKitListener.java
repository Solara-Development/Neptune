package dev.lrxh.neptune.feature.customkit.listener;

import dev.lrxh.neptune.feature.customkit.queue.CustomKitQueueService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class CustomKitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CustomKitQueueService.get().unhost(event.getPlayer().getUniqueId());
    }
}
