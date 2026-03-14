package fr.mrmicky.fastboard;

import fr.mrmicky.fastboard.adventure.FastBoard;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FastManager {
    protected final FastAdapter fastAdapter;
    protected final ConcurrentHashMap<UUID, FastBoard> boards;

    public FastManager(JavaPlugin plugin, FastAdapter fastAdapter) {
        this.fastAdapter = fastAdapter;
        this.boards = new ConcurrentHashMap<>();
        plugin.getServer().getPluginManager().registerEvents(new FastListener(this), plugin);
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new FastRunnable(this), 0, 4L);
    }
}
