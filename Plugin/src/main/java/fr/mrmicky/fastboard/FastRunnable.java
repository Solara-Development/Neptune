package fr.mrmicky.fastboard;

import fr.mrmicky.fastboard.adventure.FastBoard;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@AllArgsConstructor
public class FastRunnable implements Runnable {
    private FastManager manager;

    @Override
    public void run() {
        Iterator<Map.Entry<UUID, FastBoard>> iterator = manager.boards.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, FastBoard> entry = iterator.next();
            Player player = Bukkit.getPlayer(entry.getKey());

            if (player == null || !player.isOnline()) {
                iterator.remove();
                continue;
            }

            FastBoard board = entry.getValue();

            Component title = getSafeTitle(player);
            List<Component> lines = getSafeLines(player);

            board.updateTitle(title);
            board.updateLines(lines);
        }
    }

    private Component getSafeTitle(Player player) {
        try {
            return manager.fastAdapter.getTitle(player);
        } catch (Exception e) {
            return Component.text("Default Title"); // Fallback title
        }
    }

    private List<Component> getSafeLines(Player player) {
        try {
            return manager.fastAdapter.getLines(player);
        } catch (Exception e) {
            return new ArrayList<>(); // Fallback to an empty list of lines
        }
    }
}
