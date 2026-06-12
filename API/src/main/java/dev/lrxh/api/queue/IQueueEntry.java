package dev.lrxh.api.queue;

import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.utils.ITime;

import java.util.UUID;

public interface IQueueEntry {
    UUID getUuid();

    IKit getKit();

    ITime getTime();
}
