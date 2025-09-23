package fr.mrmicky.fastboard;

import dev.lrxh.neptune.utils.CC;
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

            String rawTitle = getSafeTitle(player);
            List<String> rawLines = getSafeLines(player);

            board.updateTitle(CC.color(rawTitle));

            List<Component> newLines = new ArrayList<>();
            for (String line : rawLines) {
                newLines.add(CC.color(line));
            }
            board.updateLines(newLines);
        }
    }

    private String getSafeTitle(Player player) {
        try {
            return manager.fastAdapter.getTitle(player);
        } catch (Exception e) {
            return "Default Title"; // Fallback title
        }
    }

    private List<String> getSafeLines(Player player) {
        try {
            return manager.fastAdapter.getLines(player);
        } catch (Exception e) {
            return Collections.emptyList(); // Fallback to an empty list of lines
        }
    }
}
