package dev.lrxh.neptune.utils.menu.impl;

import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class DisplayButton extends Button {
    private final ItemStack itemStack;
    private final String name;
    private final List<String> lore;
    private final Consumer<Player> action;

    public DisplayButton(int slot, Material itemStack, String name) {
        super(slot, false);
        this.itemStack = new ItemStack(itemStack);
        this.name = name;
        this.lore = null;
        this.action = null;
    }

    public DisplayButton(int slot, Material itemStack, String name, Consumer<Player> action) {
        super(slot, false);
        this.itemStack = new ItemStack(itemStack);
        this.name = name;
        this.lore = null;
        this.action = action;
    }

    public DisplayButton(int slot, Material itemStack, String name, List<String> lore, Consumer<Player> action) {
        super(slot, false);
        this.itemStack = new ItemStack(itemStack);
        this.name = name;
        this.lore = lore;
        this.action = action;
    }

    public DisplayButton(int slot, ItemStack itemStack, String name) {
        super(slot, false);
        this.itemStack = new ItemStack(itemStack);
        this.name = name;
        this.lore = null;
        this.action = null;
    }

    public DisplayButton(int slot, ItemStack itemStack) {
        super(slot, false);
        this.itemStack = new ItemStack(itemStack);
        this.name = null;
        this.lore = null;
        this.action = null;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        ItemBuilder builder = new ItemBuilder(itemStack);
        if (name != null) builder.name(name);
        if (lore != null) builder.lore(lore);
        return builder.build();
    }


    @Override
    public void onClick(ClickType type, Player player) {
        if (action == null) return;

        action.accept(player);
    }
}
