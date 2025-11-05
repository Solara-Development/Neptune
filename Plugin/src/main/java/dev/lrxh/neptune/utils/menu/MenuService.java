package dev.lrxh.neptune.utils.menu;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class MenuService {
    private static MenuService instance;
    private final HashMap<UUID, Menu> openedMenus;

    public MenuService() {
        this.openedMenus = new HashMap<>();
    }

    public static MenuService get() {
        if (instance == null) instance = new MenuService();

        return instance;
    }

    public void add(Player player, Menu menu) {
        openedMenus.put(player.getUniqueId(), menu);
    }

    public Menu get(Player player) {
        return openedMenus.get(player.getUniqueId());
    }

    public void remove(Player player) {
        openedMenus.remove(player.getUniqueId());
    }
}
