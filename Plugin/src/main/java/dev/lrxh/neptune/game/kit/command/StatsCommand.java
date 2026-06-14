package dev.lrxh.neptune.game.kit.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.game.kit.menu.StatsMenu;
import org.bukkit.entity.Player;

public class StatsCommand {

    @Command(name = "", desc = "")
    public void open(@Sender Player player) {
        new StatsMenu(player).open(player);
    }

    @Command(name = "", desc = "", usage = "<player>")
    public void statsOthers(@Sender Player player, Player target) {
        new StatsMenu(target).open(player);
    }
}