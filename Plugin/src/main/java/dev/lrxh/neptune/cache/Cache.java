package dev.lrxh.neptune.cache;

import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;


@Getter
@Setter
public class Cache {
    private Location spawn;

    public Cache() {
        load();
    }

    public void setSpawn(Location location) {
        spawn = location;
        save();
    }

    public void load() {
        spawn = LocationUtil.deserialize(SettingsLocale.SPAWN_LOCATION.getString());
    }

    public void save() {
        SettingsLocale.SPAWN_LOCATION.set(LocationUtil.serialize(spawn));
        SettingsLocale.SPAWN_LOCATION.getConfigFile().save();
    }
}
