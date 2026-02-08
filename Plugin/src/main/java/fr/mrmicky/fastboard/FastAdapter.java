package fr.mrmicky.fastboard;

import java.util.List;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

public interface FastAdapter {
    Component getTitle(Player player);

    List<Component> getLines(Player player);
}
