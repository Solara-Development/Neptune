package fr.mrmicky.fastboard;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public interface FastAdapter {
    Component getTitle(Player player);

    List<Component> getLines(Player player);
}
