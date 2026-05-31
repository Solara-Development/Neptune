package dev.lrxh.neptune.feature.event.task;

import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.event.EventService;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;

public class EventScheduleTask extends NeptuneRunnable {
    private int timer;

    public EventScheduleTask() {
        this.timer = SettingsLocale.EVENT_AUTO_SCHEDULE_INTERVAL.getInt();
    }

    @Override
    public void run() {
        if (EventService.get().getActiveEvent() != null) return;
        timer--;
        if (timer <= 0) {
            EventService.get().autoStart();
            timer = SettingsLocale.EVENT_AUTO_SCHEDULE_INTERVAL.getInt();
        }
    }
}
