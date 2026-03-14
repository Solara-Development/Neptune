package dev.lrxh.neptune.utils.menu;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ServerUtils;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Menu {
    @Getter
    private final int size;
    @Getter
    private final boolean updateOnClick;
    private final Component title;
    private final Filter filter;

    @Getter
    @Setter
    private boolean updateEveryTick;

    private final Map<Integer, Button> buttonCache = new HashMap<>();
    private final Map<Integer, ItemStack> itemCache = new HashMap<>();
    private final Set<Integer> dirtySlots = new HashSet<>();
    private final Set<Integer> filterSlots = new HashSet<>();
    @Getter
    @Setter
    private long updateInterval = 20L;
    private long lastUpdateTick = 0;
    @Getter
    private Inventory inventory;

    public Menu(String title, int size, Filter filter) {
        this.title = CC.color(title);
        this.size = size;
        this.filter = filter;
        this.updateOnClick = false;
        this.updateEveryTick = false;
    }
    public Menu(Component title, int size, Filter filter) {
        this.title = title;
        this.size = size;
        this.filter = filter;
        this.updateOnClick = false;
        this.updateEveryTick = false;
    }

    public Menu(int size, Filter filter) {
        this.title = Component.empty();
        this.size = size;
        this.filter = filter;
        this.updateOnClick = false;
        this.updateEveryTick = false;
    }
    public Menu(String title, int size, Filter filter, boolean updateOnClick) {
        this.title = CC.color(title);
        this.size = size;
        this.filter = filter;
        this.updateOnClick = updateOnClick;
        this.updateEveryTick = false;
    }
    public Menu(Component title, int size, Filter filter, boolean updateOnClick) {
        this.title = title;
        this.size = size;
        this.filter = filter;
        this.updateOnClick = updateOnClick;
        this.updateEveryTick = false;
    }

    public abstract List<Button> getButtons(Player player);

    public Component getTitle(Player player) {
        return Component.empty();
    }

    private void set(Inventory inventory, int slot, ItemStack itemStack) {
        if (slot < inventory.getSize()) {
            inventory.setItem(slot, itemStack);
        } else {
            ServerUtils.error("Menu: " + title + " slot (" + slot + ") is larger than inventory size: (" + inventory.getSize() + ")");
        }
    }

    public void open(Player player) {
        Bukkit.getScheduler().runTask(Neptune.get(), () -> {
            if (MenuService.get().getOpenedMenus().containsKey(player.getUniqueId())) {
                MenuService.get().remove(player);
            }

            Component title;
            if (this.title.equals(Component.empty())) {
                title = getTitle(player);
            } else {
                title = this.title;
            }

            this.inventory = Bukkit.createInventory(player, size, title);
            player.openInventory(inventory);

            fullUpdate(player);

            MenuService.get().add(player, this);
        });
    }

    public void update(Player player) {
        if (updateEveryTick) {
            long currentTick = Bukkit.getCurrentTick();
            if ((currentTick - lastUpdateTick) < updateInterval) {
                return;
            }
            lastUpdateTick = currentTick;
        }

        List<Button> newButtons = getButtons(player);

        Map<Integer, Button> newButtonMap = new HashMap<>();
        for (Button button : newButtons) {
            newButtonMap.put(button.getSlot(), button);
        }

        dirtySlots.clear();

        for (Integer slot : buttonCache.keySet()) {
            if (!newButtonMap.containsKey(slot) || hasButtonChanged(slot, newButtonMap.get(slot), player)) {
                dirtySlots.add(slot);
            }
        }

        for (Integer slot : newButtonMap.keySet()) {
            if (!buttonCache.containsKey(slot)) {
                dirtySlots.add(slot);
            }
        }

        buttonCache.clear();
        buttonCache.putAll(newButtonMap);

        applyFilter();

        for (Integer slot : dirtySlots) {
            updateSlot(player, slot);
        }

        if (!dirtySlots.isEmpty()) {
            player.updateInventory();
        }
    }

    private void fullUpdate(Player player) {
        buttonCache.clear();
        itemCache.clear();
        filterSlots.clear();
        dirtySlots.clear();

        List<Button> buttons = getButtons(player);
        for (Button button : buttons) {
            buttonCache.put(button.getSlot(), button);
        }

        applyFilter();

        for (int slot = 0; slot < size; slot++) {
            updateSlot(player, slot);
        }

        player.updateInventory();
    }

    private void updateSlot(Player player, int slot) {
        Button button = buttonCache.get(slot);

        ItemStack item;
        if (button != null) {
            item = button.getItemStack(player);
            itemCache.put(slot, item);
        } else {
            item = itemCache.get(slot);
        }

        set(inventory, slot, item);
    }

    private void applyFilter() {
        if (filter == Filter.NONE) {
            return;
        }

        Material filterMaterial = Material.getMaterial(MenusLocale.FILTER_MATERIAL.getString());
        String filterName = MenusLocale.FILTER_NAME.getString();

        switch (filter) {
            case FILL -> {
                for (int i = 0; i < size; i++) {
                    if (!buttonCache.containsKey(i)) {
                        Button filterButton = new DisplayButton(i, filterMaterial, filterName);
                        buttonCache.put(i, filterButton);
                        filterSlots.add(i);
                    }
                }
            }
            case BORDER -> {
                int rows = size / 9;
                for (int slot = 0; slot < size; slot++) {
                    if (isBorderSlot(slot, rows) && !buttonCache.containsKey(slot)) {
                        Button filterButton = new DisplayButton(slot, filterMaterial, filterName);
                        buttonCache.put(slot, filterButton);
                        filterSlots.add(slot);
                    }
                }
            }
        }
    }

    private boolean isBorderSlot(int slot, int rows) {
        int row = slot / 9;
        int col = slot % 9;
        return row == 0 || row == rows - 1 || col == 0 || col == 8;
    }

    protected boolean hasButtonChanged(int slot, Button newButton, Player player) {
        return !filterSlots.contains(slot);
    }

    public Button getButton(int slot) {
        return buttonCache.get(slot);
    }
}
