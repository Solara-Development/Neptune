package dev.lrxh.neptune.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.entity.Player;

public class LeaveCommand {

    @Command(name = "", desc = "")
    public void leave(@Sender Player player) {
        Profile profile = API.getProfile(player);
        profile.forfeit();
    }
}
