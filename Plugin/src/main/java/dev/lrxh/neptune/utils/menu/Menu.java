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

import java.util.List;

public abstract class Menu {
    @Getter
    private final int size;
    @Getter
    private final boolean updateOnClick;
    private final Component title;
    private final Filter filter;
    private List<Button> buttons;

    @Getter
    @Setter
    private boolean updateEveryTick;

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

            Inventory inventory = Bukkit.createInventory(player, size, title);
            this.inventory = inventory;
            player.openInventory(inventory);

            update(player);

            MenuService.get().add(player, this);
        });
    }

    public void update(Player player) {
        buttons = getButtons(player);
        switch (filter) {
            case FILL -> {
                for (int i = 0; i < inventory.getSize(); i++) {
                    if (getButton(i) == null) {
                        buttons.add(new DisplayButton(i, Material.getMaterial(MenusLocale.FILTER_MATERIAL.getString()), MenusLocale.FILTER_NAME.getString()));
                    }
                }
            }
            case BORDER -> {
                int rows = size / 9;
                int columns = 9;

                for (int i = 0; i < size; i++) {
                    int row = i / columns;
                    int column = i % columns;

                    if (row == 0 || row == rows - 1 || column == 0 || column == columns - 1) {
                        if (getButton(i) == null) {
                            buttons.add(new DisplayButton(i, Material.getMaterial(MenusLocale.FILTER_MATERIAL.getString()), MenusLocale.FILTER_NAME.getString()));
                        }
                    }
                }
            }
            case NONE -> {
            }
        }


        for (Button button : buttons) {
            set(inventory, button.getSlot(), button.getItemStack(player));
        }
        player.updateInventory();
    }

    public Button getButton(int slot) {
        for (Button button : buttons) {
            if (button.getSlot() == slot) return button;
        }

        return null;
    }
}
