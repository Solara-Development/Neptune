package dev.lrxh.neptune.feature.divisions.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.feature.divisions.menu.DivisionsMenu;
import org.bukkit.entity.Player;

public class DivisionsCommand {

    @Command(name = "", desc = "")
    public void open(@Sender Player player) {
        new DivisionsMenu().open(player);
    }
}
