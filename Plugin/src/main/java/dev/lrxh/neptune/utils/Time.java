package dev.lrxh.neptune.utils;

import dev.lrxh.api.utils.ITime;

public class Time implements ITime {
    private long oldTime;
    private long lastCurrentTime;
    private boolean stop;

    public Time() {
        this.oldTime = System.currentTimeMillis();
        this.stop = false;
    }

    public String formatTime() {
        if (!stop) {
            lastCurrentTime = System.currentTimeMillis();
        }

        long elapsedTime = lastCurrentTime - oldTime;

        long minutes = (elapsedTime / 1000) / 60;
        long seconds = (elapsedTime / 1000) % 60;

        return (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    public String formatSecondsMillis() {
        if (!stop) {
            lastCurrentTime = System.currentTimeMillis();
        }

        long elapsedTime = lastCurrentTime - oldTime;

        long millis = (elapsedTime % 1000) / 10;
        long seconds = (elapsedTime / 1000) % 60;

        return (seconds < 10 ? "0" : "") + seconds + "." + (millis < 10 ? "0" : "") + millis + "s";
    }

    public long getElapsed() {
        return System.currentTimeMillis() - oldTime;
    }

    public void setZero() {
        this.oldTime = 0;
        this.lastCurrentTime = 0;
    }

    public void setStop(boolean stop) {
        this.stop = stop;

        if (!stop) {
            this.oldTime = System.currentTimeMillis();
        }
    }
}
