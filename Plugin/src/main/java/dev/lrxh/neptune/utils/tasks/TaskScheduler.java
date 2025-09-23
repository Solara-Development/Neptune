package dev.lrxh.neptune.utils.tasks;

import dev.lrxh.neptune.Neptune;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskScheduler {
    private static TaskScheduler instance;
    private final List<NeptuneRunnable> tasks;
    private final TaskListener listener;

    public TaskScheduler() {
        this.tasks = new CopyOnWriteArrayList<>();
        this.listener = new TaskListener();
        Neptune.get().getServer().getPluginManager().registerEvents(listener, Neptune.get());
    }

    public static TaskScheduler get() {
        if (instance == null) instance = new TaskScheduler();

        return instance;
    }

    public void startTask(NeptuneRunnable task, long delay, long period) {
        tasks.add(task);
        task.runTaskTimer(Neptune.get(), delay, period);
    }

    public void startTask(NeptuneRunnable task) {
        tasks.add(task);
        task.runTask(Neptune.get());
    }

    public void startTaskCurrentTick(NeptuneRunnable task) {
        listener.getRunnables().add(task);
    }

    public void startTaskLater(NeptuneRunnable task, long delay) {
        tasks.add(task);
        task.runTaskLater(Neptune.get(), delay);
    }

    public void stopAllTasks() {
        for (NeptuneRunnable task : tasks) {
            task.stop();
        }
    }

    public void removeTask(NeptuneRunnable task) {
        tasks.remove(task);
    }
}
