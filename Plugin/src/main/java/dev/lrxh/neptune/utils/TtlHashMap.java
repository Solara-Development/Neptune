package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class TtlHashMap<K, V> extends HashMap<K, V> {
    private final Map<K, TtlAction> actions = new HashMap<>();
    private final long leaveTime;

    public TtlHashMap(long delay) {
        this.leaveTime = delay;
    }

    public void put(K key, V value, TtlAction action) {
        super.put(key, value);
        actions.put(key, action);
        scheduleRemoval(key);
    }

    public void onExpire(K key) {
        TtlAction action = actions.get(key);
        if (action != null) {
            action.getConsumer().accept(Bukkit.getPlayer(action.getPlayerUUID()));
            actions.remove(key);
        }
    }

    public boolean contains(K key) {
        return containsKey(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        K castKey = (K) key;
        if (key != null && actions.containsKey(castKey)) {
            NeptuneRunnable runnable = actions.get(castKey).getRunnable();
            if (runnable != null) {
                runnable.stop();
            }
            actions.remove(castKey);
        }
        return super.remove(key);
    }

    private void scheduleRemoval(K key) {
        TtlAction action = actions.get(key);
        if (action != null) {
            action.setRunnable(new NeptuneRunnable() {
                @Override
                public void run() {
                    remove(key);
                    onExpire(key);
                }
            }, leaveTime);
        }
    }
}
