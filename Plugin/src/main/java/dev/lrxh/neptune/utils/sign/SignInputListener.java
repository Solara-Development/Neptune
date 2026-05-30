package dev.lrxh.neptune.utils.sign;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import dev.lrxh.neptune.Neptune;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SignInputListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.UPDATE_SIGN) return;

        UUID uuid = event.getUser().getUUID();
        SignInputMenu menu = SignInputMenu.sessions.remove(uuid);
        if (menu == null) return;

        String[] lines = new WrapperPlayClientUpdateSign(event).getTextLines();
        String input = (lines != null && lines.length > 0 && lines[0] != null) ? lines[0].trim() : "";

        Bukkit.getScheduler().runTask(Neptune.get(), () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.sendBlockChange(menu.location, menu.location.getBlock().getBlockData());
            if (!input.isEmpty()) menu.callback.accept(input);
        });
    }
}
