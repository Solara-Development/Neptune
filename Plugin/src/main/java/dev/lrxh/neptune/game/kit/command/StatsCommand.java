package dev.lrxh.neptune.game.kit.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.OptArg;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.game.kit.menu.StatsMenu;
import org.bukkit.entity.Player;

public class StatsCommand {

    @Command(name = "", desc = "", usage = "[player]")
    public void stats(@Sender Player player, @OptArg Player target) {
        if (target == null) {
            target = player;
        }
        new StatsMenu(target).open(player);
    }
}