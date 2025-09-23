package dev.lrxh.neptune.utils.tasks;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

@Getter
public class TaskListener implements Listener {
    private final Set<NeptuneRunnable> runnables;

    public TaskListener() {
        this.runnables = new HashSet<>();
    }

    @EventHandler
    public void onTickEnd(ServerTickEndEvent event) {
        for (NeptuneRunnable runnable : new HashSet<>(runnables)) {
            runnable.run();
            runnables.remove(runnable);
        }
    }
}
